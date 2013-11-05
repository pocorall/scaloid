import sbt._
import Keys._

import java.beans.Introspector
import java.lang.reflect.{Array => JavaArray, _}
import java.util.jar.JarFile
import org.reflections._
import org.reflections.scanners._
import org.reflections.util._
import scala.collection.JavaConversions._


object AndroidClassExtractor extends JavaConversionHelpers {

  private val sourceJars: List[JarFile] =
    getClass.getClassLoader.getResources("android").toList.map {
      binUrl =>
        val binFile = new File(binUrl.toString.split("/|!").tail.dropRight(2).mkString("/", "/", ""))
        val basePath = binFile.getParentFile.getParentFile
        new JarFile(basePath / "srcs" / (binFile.base + "-sources.jar"))
    }

  private def sourceInputStream(cls: Class[_]): Option[java.io.InputStream] = {
    val filename = cls.getName.split('$').head.replace(".", "/") + ".java"
    sourceJars.map {
      sourceJar =>
        sourceJar.getJarEntry(filename) match {
          case null => Nil
          case entry => List(sourceJar.getInputStream(entry))
        }
    }.flatten.headOption
  }

  private def sourceExists(cls: Class[_]) = sourceInputStream(cls).isDefined

  private def extractSource(cls: Class[_]): String = {
    val is = sourceInputStream(cls).get
    val bytes = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
    new String(bytes)
  }

  private def fixClassParamedType(tpe: ScalaType) =
    tpe.copy(params = tpe.params.map {
      t =>
        if (t.isVar && t.bounds.head.name == "Any") {
          t.copy(bounds = Seq(ScalaType("AnyRef")))
        } else t
    })

  private def toAndroidClass(cls: Class[_]) = {

    val source = extractSource(cls)

    val superClass = getSuperclass(cls)

    val fullName = cls.getName

    val clsName = simpleClassName(fullName)
    val tpe = fixClassParamedType(toScalaType(cls))
    val pkg = fullName.split('.').init.mkString(".")
    val parentType = Option(cls.getGenericSuperclass)
      .map(toScalaType)
      .filter(_.name.startsWith("android"))

    val superProps: Set[String] =
      superClass.map {
        Introspector.getBeanInfo(_)
          .getPropertyDescriptors
          .map(propDescSignature).toSet[String]
      }.getOrElse(Set())

    val superMethods: Set[String] =
      superClass.map {
        _.getMethods.map(methodSignature).toSet
      }.getOrElse(Set())

    def toAndroidMethod(m: Method): AndroidMethod = {
      val name = m.getName
      val retType = AndroidClassExtractor.toScalaType(m.getGenericReturnType)
      val argTypes = m.getGenericParameterTypes.toSeq.map(AndroidClassExtractor.toScalaType(_))
      val paramedTypes = (retType +: argTypes).filter(_.isVar).distinct

      AndroidMethod(name, retType, argTypes, paramedTypes, isAbstract(m), isOverride(m))
    }

    def isListenerSetterOrAdder(m: Method): Boolean = {
      val name = m.getName
      name.matches("^(set|add).+Listener$") && !superMethods(methodSignature(m))
    }

    def hasIntentAsParam(m: Method): Boolean = {
      val params = m.getParameterTypes
      if (params.length > 0) "android.content.Intent".equals(m.getParameterTypes.apply(0).getName) && !superMethods(methodSignature(m))
      else false
    }

    def isCallbackMethod(m: Method): Boolean =
      isAbstract(m) && !m.getName.startsWith("get")

    def extractMethodsFromListener(callbackCls: Class[_]): List[AndroidMethod] =
      callbackCls.getMethods.view
        .filter(isCallbackMethod)
        .map(toAndroidMethod)
        .toList
        .sortBy(_.name)

    def propName(name: String) = {
      val s = "^(get|is|set)".r.replaceAllIn(name, "")
      if (s.take(3).matches("[A-Z]{3}")) s
      else s.head.toLower + s.tail
    }

    def isGetter(name: String) = name.matches("^(get|is)[^a-z].*")

    def isSetter(name: String) = name.matches("^set[^a-z].*")

    val props: Seq[AndroidProperty] = {
      val clsMethods = cls.getMethods
      val accessors = clsMethods.filter {
        m =>
          val name = m.getName
          val arity = m.getParameterTypes.length
          !superMethods(methodSignature(m)) && (
            (arity == 0 && isGetter(name)) || (arity == 1 && isSetter(name))
            )
      }
        .filter {
        m =>
          (!cls.getName.endsWith("Service") || !m.getName.equals("setForeground")) && // Android 2.1.1 has a weird undocumented method. manually ingore this.
            (!cls.getName.endsWith("WebView") || !m.getName.equals("getZoomControls")) //https://github.com/pocorall/scaloid/issues/56
      }

      val allMethodNames = clsMethods.map(_.getName).toSet

      accessors.groupBy(m => propName(m.getName)).map {
        case (name, methods) =>
          val (_setters, _getters) = methods.filter {
            !_.toGenericString.contains("static")
          }.partition(_.getName.startsWith("set"))
          val setters = _setters.map(toAndroidMethod).sortBy(_.argTypes.head.name)
          val getter = _getters.map(toAndroidMethod).headOption
          def superGetterExists =
            superClass.map(_.getMethods.find(_.getName == "get" + name.capitalize)).flatten.nonEmpty

          if (setters.isEmpty && (getter.isEmpty || getter.get.name.startsWith("is"))) None
          else {
            val nameClashes = allMethodNames(name) || superGetterExists
            val tpe = getter.map(_.retType).getOrElse(setters.head.argTypes.head)
            val switch = if (name.endsWith("Enabled")) Some(name.replace("Enabled", "").capitalize)
            else if (name.equals("enabled")) Some("") else None

            Some(AndroidProperty(name, tpe, getter, setters, switch, nameClashes))
          }
      }.flatten.toSeq.sortBy(_.name)
    }


    def toAndroidIntentMethods(method: Method): AndroidIntentMethod = {
      val am = toAndroidMethod(method)
      val args = am.argTypes.tail
      AndroidIntentMethod(method.getName, am.retType, args, args.length == 0)
    }

    def toAndroidListeners(method: Method): Seq[AndroidListener] = {
      val setter = method.getName
      val setterArgTypes = method.getGenericParameterTypes.toSeq
      val listenerCls: Class[_] = setterArgTypes.collectFirst {
        case c: Class[_] if c.getName.matches(".+(Listener|Manager|Observer|Watcher).*") => c
      } match {
        case Some(c) => c
        case None => classOf[Any] // Failed to find listener argument by type
      }
      val callbackMethods = extractMethodsFromListener(listenerCls)
      val androidCallbackMethods =
        callbackMethods.map {
          cm =>
            AndroidCallbackMethod(
              cm.name,
              cm.retType,
              cm.argTypes,
              false
            )
        }

      def listenerName(am: AndroidMethod, callbacks: Seq[AndroidCallbackMethod], setter: String) = {
        if (callbacks.length != 1) am.name
        else {
          val transforms = List[String => String](
            _.replace("$", "."),
            "(^set|^add|Listener$|ElementListener$)".r.replaceAllIn(_, ""),
            ((s: String) => s.head.toLower + s.tail),
            "Changed$".r.replaceAllIn(_, "Change")
          ).reduce(_ andThen _)

          val specificName = transforms(setter)
          val generalName = "^on".r.replaceAllIn(am.name, "")

          if (specificName.length > am.name.length && specificName.contains(generalName))
            specificName
          else
            am.name
        }
      }

      callbackMethods.map {
        cm =>
          AndroidListener(
            listenerName(cm, androidCallbackMethods, setter),
            callbackMethods.find(_.name == cm.name).get.retType,
            cm.argTypes,
            cm.argTypes.nonEmpty,
            setter,
            setterArgTypes.map(toScalaType),
            toScalaType(listenerCls).name,
            androidCallbackMethods.map {
              icm => if (icm.name == cm.name) icm.copy(hasBody = true) else icm
            }
          )
      }.filter(_.isSafe)
    }

    def resolveListenerDuplication(listeners: Seq[AndroidListener]) =
      listeners map {
        l =>
          if (listeners.filter(l2 => l.name == l2.name && l.setterArgTypes == l2.setterArgTypes).length > 1) {
            val t = "(^set|^add|Listener$)".r.replaceAllIn(l.setter, "")
            l.copy(name = t.head.toLower + t.tail)
          } else l
      }

    val constructorNames: Map[List[String], List[String]] = {
      val constRegex = ("public +" + clsName + """(?:\<[\w\<\>\[\]]+)? *\(([^)]*)\) *(?:\{?|[^;])""").r
      val argRegex = """(.+?) +([a-z][^\[,. ]*)(?:,|$)""".r

      constRegex.findAllIn(source).matchData.map(_.group(1)).filter(_.nonEmpty).map {
        cGroup =>
          argRegex.findAllIn(cGroup).matchData.map {
            pMatch =>
              val List(tpe, name) = List(1, 2).map(pMatch.group(_).trim)
              (tpe, name)
          }.toList.unzip
      }.toMap
    }

    // Find constructor not by fully qualified name
    def looseConstructorLookup(types: List[String]): Option[List[String]] =
      constructorNames.find {
        case (key, _) =>
          types.zip(key).forall {
            case (t, k) => t.endsWith(k)
          }
      }.map(_._2)

    def toScalaConstructor(cons: Constructor[_]): ScalaConstructor = {

      val isVarArgs = cons.isVarArgs

      def isImplicit(a: Argument) = {
        a.tpe.simpleName == "Context"
      }

      val javaTypes = cons.getGenericParameterTypes.toList
      val typeStrs = javaTypes.reverse match {
        case Nil => Nil
        case last :: init => (toTypeStr(last, isVarArgs, true) :: init.map(toTypeStr(_, isVarArgs, false))).reverse
      }

      val args = typeStrs match {
        case Nil => Nil
        case _ =>
          val paramNames = constructorNames.get(typeStrs) match {
            case Some(ns) => ns
            case None => looseConstructorLookup(typeStrs).get
          }
          val types = javaTypes.map(toScalaType)

          paramNames.zip(types).map {
            case (n, t) => Argument(n, t)
          }
      }

      val (implicits, explicits) = args.partition(isImplicit)
      val paramedTypes = (tpe.params ++ args.map(_.tpe)).filter(_.isVar).distinct

      ScalaConstructor(args, implicits, explicits, paramedTypes, cons.isVarArgs)
    }

    def getHierarchy(c: Class[_], accu: List[String] = Nil): List[String] =
      if (c == null) accu
      else getHierarchy(c.getSuperclass, simpleClassName(c) :: accu)

    val listeners = resolveListenerDuplication(
      cls.getMethods.view
        .filter(isListenerSetterOrAdder)
        .map(toAndroidListeners)
        .flatten.toSeq
        .sortBy(_.name))

    val intentMethods = cls.getMethods.view.filter(hasIntentAsParam).map(toAndroidIntentMethods).toSeq.sortBy(m => m.name + m.argTypes.length)

    val constructors = cls.getConstructors
      .map(toScalaConstructor)
      .toSeq
      .sortBy(c => (c.explicitArgs.length, -c.implicitArgs.length))

    val isA = getHierarchy(cls).toSet

    val hasBlankConstructor = constructors.exists(_.explicitArgs.length == 0)

    AndroidClass(clsName, pkg, tpe, parentType, constructors, props, listeners, intentMethods, isA, isAbstract(cls), isFinal(cls), hasBlankConstructor)
  }

  def extractTask = (moduleName, baseDirectory, streams) map {
    (mName, baseDir, s) =>
      if (mName == "parent") Map[String, AndroidClass]()
      else {
        s.log.info("Extracting class info from Android...")

        val classLoaders =
          List(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader())

        val inputFilter = new FilterBuilder()
          .include(FilterBuilder.prefix("android"))
          .exclude(".*Honeycomb.*") // excluding some classes that depends on the Android library ABOVE 2.1.1
          .exclude(".*Compat.*")

        val r = new Reflections(new ConfigurationBuilder()
          .addClassLoaders(classLoaders: _*)
          .setScanners(new SubTypesScanner(false), new ResourcesScanner())
          .setUrls(ClasspathHelper.forClassLoader(classLoaders: _*))
          .filterInputsBy(inputFilter))

        val clss = asScalaSet(r.getSubTypesOf(classOf[java.lang.Object]))
        val res = clss.toList
          .filter {
          s.log.info("Excluding inner classes for now - let's deal with it later")
          !_.getName.contains("$")
        }
          .filter {
          n =>
            val name = n.toString
            !name.contains("webkit") || name.contains("WebView") // excluding android.webkit.* in Android 2.1.1, which is deprecated
        }
          .filter(sourceExists)
          .map(toAndroidClass)
          .map(c => c.tpe.name -> c)
          .toMap

        val values = res.values.toList
        s.log.info("Done.")
        s.log.info("Classes: " + values.length)
        s.log.info("Properties: " + values.map(_.properties).flatten.length)
        s.log.info("Listeners: " + values.map(_.listeners).flatten.length)
        s.log.info("Constructors: " + values.map(_.constructors).flatten.length)
        res
      }
  }
}

