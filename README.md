<p align="center"><img src="http://o-n2.com/scaloid_logo.png"></p>

# Less painful Android development with Scala

Scala is cool. [Writing Android application with Scala](#need-help-using-scala-language-in-your-android-project) is also a cool idea. Because Android exposes Java API, we need some utility library to leverage full power of Scala. Scaloid is a library aimed to simplify your Android code.

For example, the code block shown below:

	val button = new Button(context)
	button.setText("send")
	button.setOnClickListener(new OnClickListener() {
	  def onClick(v: View) {
		sendMessage()
	  }
	})
	layout.addView(button)

is reduced to:	
	
	layout += $Button("send", sendMessage())


### Benefits
 * **Write elegant Android software**<br/>
   Read later part of this document to see how Scaloid greatly improve your code.
 * **Simple to use**<br/> 
   This is a single-file project. Just copy-and-paste `common.scala` into your project!
 * **Compatible with your legacy code**<br/>
   You can use both style of Scaloid and plain-old Java Android API. You can gradually improve your legacy code.
 * **Maintained actively**<br/>
   This project is originally created to be used for [my own Android app](https://play.google.com/store/apps/details?id=com.tocplus.client.android). The first principle of this project is "working right".
   
### Demos

If you want to see how Scaloid can be used in action, check a [scala port of apidemos app](https://github.com/pocorall/scaloid-apidemos).

##### Need help using Scala language in your Android project?
Please refer https://github.com/rohansingh/android-scala-test.


## UI Layout without XML

Android SDK leverages XML to build UI layouts. However, XML considered still a bit verbose, and lacks programmability. Scaloid composes UI layout in Scala DSL style, therefore achieve both clearity and programmability. For example, suppose a legacy XML layout as shown below:

    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:orientation="vertical" android:layout_width="match_parent"
            android:layout_height="wrap_content" android:padding="20dip">
        <TextView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:text="Sign in"
                android:layout_marginBottom="25dip" android:textSize="24.5sp"/>
        <TextView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:text="ID"/>
        <EditText android:layout_width="match_parent"
                android:layout_height="wrap_content" android:id="@+id/userId"
                android:inputType="textUri"/>
        <TextView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:text="Password"/>
        <EditText android:layout_width="match_parent"
                android:layout_height="wrap_content" android:id="@+id/password"
                android:inputType="textPassword"/>
        <Button android:layout_width="match_parent"
                android:layout_height="wrap_content" android:id="@+id/signin"
                android:text="Sign in"/>
        <LinearLayout android:orientation="horizontal"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
            <Button android:text="Help" android:id="@+id/help"
                    android:layout_width="match_parent" android:layout_height="wrap_content"/>
            <Button android:text="Sign up" android:id="@+id/signup"
                    android:layout_width="match_parent" android:layout_height="wrap_content"/>
        </LinearLayout>
    </LinearLayout>

is reduced to:

    new $LinearLayout {
	  orientation = VERTICAL
	  val id = $EditText()
	  val pass = $EditText() inputType TEXT_PASSWORD
	  +=($TextView("Sign in").textSize(24.5 sp).layout.marginBottom(25 dip).end)
	  +=($TextView("ID")) += id += $TextView("Password") += pass
	  +=($Button("Sign in"))
	  +=(new $LinearLayout {
	    +=($Button("Help")) 
		+=($Button("Sign up"))
	  })
    }.padding(20 dip)

The layout description shown above is highly programmable. You can easily wire your logic into the layout:
	
<pre><code>new $LinearLayout {
  orientation = VERTICAL
  val id = $EditText()
  val pass = $EditText() inputType TEXT_PASSWORD
  +=($TextView("Sign in").textSize(24.5 sp).layout.marginBottom(25 dip).end)
  +=($TextView("ID")) += id += $TextView("Password") += pass
  +=($Button("Sign in"<b><i>, signin(id.text, pass.text)</i></b>))
  +=(new $LinearLayout {
    +=($Button("Help"<b><i>, openUri("http://help.url")</i></b>))
    +=($Button("Sign up"<b><i>, openUri("http://signup.uri")</i></b>)
  })
}.padding(20 dip)
</code></pre>

That's it!	
		
## Implicit conversions
Scaloid employs several implicit conversions. Some of available implicit conversions are shown below:

##### Uri conversion

    String => Uri
	
The functions such as [play ringtones](#play-ringtones) `play()` or [open URIs](#open-uris) `openUri()` takes an instance of `Uri` as a parameter. However, we frequently have URIs as a `String`. Scaloid implicitly converts `String` into `Uri`. Therefore, you can freely use `String` when you play a ringtone:

    play("content://media/internal/audio/media/50")
	
, open a URI:

    openUri("http://google.com")

, or wherever you want.	
	
##### Resource IDs
	
	Int => CharSequence
	Int => Drawable
	
Android API provides two versions of methods for string resources; One for `CharSequence`, the other for `Int` as a resource ID. If you write a functions that handles Android resource, you also have to expose methods for every combinations of two versions of resources:


    def alert(titleId:Int, textId:Int)(implicit context:Context) = {
      alert(context.getText(titleId), context.getText(textId))
    }
    
    def alert(titleId:Int, text:CharSequence)(implicit context:Context) = {
      alert(context.getText(titleId), text)
    }
    
    def alert(titleId:CharSequence, textId:Int)(implicit context:Context) = {
      alert(title, context.getText(textId))
    }
    
    def alert(title:CharSequence, text:CharSequence) = ...

This is not a smart way. Write just one method that defines the logic:

    def alert(title:CharSequence, text:CharSequence) = ...
	
Then Scaloid implicit conversions will take care about these resource type conversions.

	
##### Listeners	
	
	( => Any) => OnClickListener
	( => Boolean) => OnLongClickListener
	((View, Boolean) => Any) => OnFocusChangeListener
	( => Boolean) => OnEditorActionListener
	( => Boolean) => OnKeyListener
	((CharSequence, Int, Int, Int) => Any) => TextWatcher

In Scaloid, listeners can be described in three ways: implicit conversions wich is shown above, [rich class](#rich-classes), and $-ed class. We recommend to use rich class or $-ed class for listeners. We provide implicit conversions for listeners as an auxiliary way.

##### Runnable
	
	( => Any) => Runnable

`Runnable` also covered with [rich](#rich-classes) and $-ed classes.
	
There are more implicit conversions available. Check the source code as needed.
	
## Context as an implicit parameter
Many methods in Android API requires an instance of a class `Context`. Providing this for every method calls results a clumsy code. We employs an implicit parameter to elliminate this. Just declare an implicit value that represents current context:

    implicit val context = ...

or just extend trait `ContextUtil`, which defines it for you. Then the codes that required `Context` becomes much simpler, for example:


##### Intent

    new Intent(context, classOf[MyActivity])

is reduced to:

    $Intent[MyActivity]

##### Starting and stopping service

    startService(new Intent(context, classOf[MyService]))
    stopService(new Intent(context, classOf[MyService]))	

is reduced to:

    startService[MyService]
    stopService[MyService]
	

##### Toast
    
    Toast.makeText(context, "hi, there!", Toast.LENGTH_SHORT).show()

is reduced to:

    toast("hi, there!")
   
##### Dialog

    ProgressDialog.show(context, "Dialog", "working...", true)

is reduced to:

    spinnerDialog("Dialog", "working...")

##### Pending intent

    PendingIntent.getActivity(context, 0, new Intent(context, classOf[MyActivity]), 0)
    PendingIntent.getService(context, 0, new Intent(context, classOf[MyService]), 0)

is reduced to:

    pendingActivity[MyActivity]
    pendingService[MyService]

##### DefaultSharedPreferences

    PreferenceManager.getDefaultSharedPreferences(context)

is reduced to:

    defaultSharedPreferences

##### Play ringtones

Just play the default notification ringtone:

    play()
	
, specify ringtone resources as a `String`:

    play("content://media/internal/audio/media/50")
	
, or specify a resource `Uri`:
	
	play(alarmSound)

##### Open URIs

This opens a web browser (or another view assigned to http protocol).

   	openUri("http://google.com")

	
##### System services

Getting system service objects become much simpler.

    val vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
    vibrator.vibrate(500)

is reduced to:

    vibrator.vibrate(500)	

Under the hood, Scaloid defines a function `vibrator` like this:

    implicit def vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
	
All of the system service accessors available in Android API level 8 are defined.


## Rich classes

For an Android Class `Foo` for example, Scaloid defines an implicit conversion `Foo => RichFoo`. `RichFoo` defines additional method for more convenient access to `Foo`. This is a common pattern in Scala to extend existing API.


##### Class RichView

All of listener-appending methods such as `onKey()`, `onLongClick()`, and `onTouch()` are defined in `RichView`. For example:

    find[Button](R.id.search).onClick(openUri("http://google.com"))

Some conventions we employed for method naming are:

 * We omit `set...`, `add...`, and `...Listener` from the method name, which is less significant.
 * Every methods has two versions of parameters overriden. One is a lazy parameter, and another is a function which has a full parameter defined in the original Android API. For example, these two usages are valid:

```
button.onTouch(info("touched"))
button.onTouch((v:View, e:MotionEvent) => info("touched a button "+v))
```	

 * Methods `add...` is abbreviated with a method `+=` if it is not a listener-appender.
	
##### Class RichTextView

Methods `beforeTextChanged()`, `onTextChanged()`, and `afterTextChanged()` are defined in `RichTextView`, which can be implicitly converted from `TextView`. It is more convenient than using `TextWatcher` directly. For example:

    inputField.beforeTextChanged(saveTextStatus())

is equivalent to:	
	
    inputField.addTextChangedListener(new TextWatcher {
      def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        saveTextStatus()
      }

      def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

      def afterTextChanged(p1: Editable) {}
    })
	
##### Other 'Rich' classes

Other `Rich...` classes are also defined to provide additional functionality by implicit conversion. Please check the source code for details.
	

## Traits

### Trait ContextUtil

Trait `ContextUtil` includes several shortcuts for frequently used android idioms.


### Trait RunOnUiThread

Android API provides `runOnUiThread()` only for class `Activity`. Trait `RunOnUiThread` provides Scala version of `runOnUiThread()` for anywhere other than `Activity`.

Instead of:

    activity.runOnUiThread {
      new Runnable() {
	    def run() {
	      debug("Running only in Activity class")
    	}
      }
    }

, extend trait `RunOnUiThread` and use it like this:

    runOnUiThread(debug("Running in any context"))

Using this and importing `scala.concurrent.ops._`, an asynchronous job can be run like this:

    spawn {
		val result = doAJobTakesSomeTime(params)
		runOnUiThread(alert("Done!", result))
	}
	
Compare the code above with the code using `AsyncTask`, which is shown below. It is a great win as it exposes your idea clealy.

    new AsyncTask[String, Void, String] {
      def doInBackground(params: Array[String]) = {
        doAJobTakesSomeTime(params)
      }

      override def onPostExecute(result: String) {
        alert("Done!", result)
      }
    }.execute("param")

This pattern can also elliminate `AsyncQueryHandler`. Compare with the [original Java code](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/view/ExpandableList2.java?av=h)
and a [Scala port](https://github.com/pocorall/scaloid-apidemos/blob/master/src/main/java/com/example/android/apis/view/ExpandableList2.scala) of ApiDemos example app.
	
### Trait UnregisterReceiverService

When you registered `BroadcastReceiver` with `Context.registerReceiver()` you have to unregister it to prevent memory leak. Trait UnregisterReceiverService handles these chores for you by just extend it for your Service.

```
class MyService extends UnregisterReceiverService {
  def func() {
    // ...
	registerReceiver(receiver, intentFilter)
	// Done! automatically unregistered at UnregisterReceiverService.onDestroy()
  }
}
```

### Trait ActivityUtil

Instead of

    findViewById(R.id.login).asInstanceOf[Button]

use this shortcut:

    find[Button](R.id.login)
	
## Logging

Unlike other logging frameworks, Android Logging API requires a `String` tag for every log calls. We elliminate this by introducing implicit parameter. Define an implicit value type of `LoggerTag` as shown:

    implicit val tag = LoggerTag("MyAppTag")

or, extend trait `TagUtil` or `ContextUtil` which defines this by default. Then you can simply log like this:

	warn("Something happened!")

Other functions for every log levels(`verbose`, `debug`, `info`, `warn`, `error`, `wtf`) are available. 

	info("hello " + world)

A `String` parameter passed with `info()` is a lazy argument, so it is evaluated only if the logging is possible. Therefore the example shown above is equivalent to:
	
	val tag = "MyAppTag"
	if(Log.isLoggable(tag, Log.INFO)) Log.i(tag, "hello " + world)


## Scala getters and setters

You can use any type of setters listed below:

* `obj.setText("Hello")` Java bean style
* `obj.text = "Hello"` Assignment style
* `obj text "Hello"` DSL style
* `obj.text("Hello")` Method calling style

Compared with Java style getters and setters, for example:

    new TextView(context) {
	  setText("Hello")
	  setTextSize(15)
	}
	
that of Scala style clearly reveals the nature of the operations as shown below:

    new $TextView {
      text = "Hello"
      textSize = 15
	}
	
Or, you can also chain the setters:

    new $TextView text "Hello" textSize 15
	
, which is a syntactic sugar of a method calling:

    new $TextView.text("Hello").textSize(15)	

We recommend "assignment style" and "DSL style". Use assignment style when you emphasize that you are assigning something, or use DSL style when the code length of the assignee is short and need to be chained.

Note: Using `.apply(String)` method on object `$TextView`, you can further reduce the code above like this:

    $TextView("Hello") textSize 15

Note 2: Currently, Assignment or DSL style setters are not covered for all setters from Android API. Check our [roadmap](#roadmap).

### Return value of setters

Unlike most setters in Android API, our setters return the object itself. This feature can be used as a syntactic sugar when setters need to be chained or a function returning some object. For example, a snippet of [Java code](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/view/ExpandableList1.java?av=h) from ApiDemos that is shown below:

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView textView = getGenericView();
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

is reduced to:

    def getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View, parent: ViewGroup): View =
      getGenericView.text = getGroup(groupPosition).toString

<sub>**Design considerations on returning values:** In C or Java, the assinment operator `=` returns a right hand side object. However, chaining assignment operator is very rarelly used in these languages. Assigning the same value to multiple variables might means that your code is badly designed (except some context such as involving intensive mathematical computations). However, in Scala DSLs, setters return a left hand side object, and chaining setters are more frequent. For example:</sub>

    getGenericView text "hello" maxHeight 8
	
### Dollar-signed($-ed) classes

If you want to use scala style getters/setters, implicit conversion do the magic on native Android objects:

    val v:TextView = ...
	v.text = "Hello"    // Valid code. Implicit conversion handles this.
	
However, if you use it in constructors, compiler cannot find correct implicit conversion:
	
    def getInstance = new TextView(context) {
	  text = "Hello"    // Compilation Error.
	}
	
Therefore, we extended Android classes with the same name prefixed with a dollar($) sign:
	
	def getInstance = new $TextView {
	  text = "Hello"    // OK.
	}
	
These classes explicitly provides the extra methods that was provided implicitly. 

Aditionally, $-ed classes supports [implicit context value](#context-as-an-implicit-parameter) and additional syntactic sugars. For example, many classes has `.apply(...)` methods for creating a new instance:

    $TextView("Hello")
    $Button("title", onClickBehavior())
	$Intent[MyActivity]

<sub>**Design considerations on making $-ed classes:** In modern programming language, using package (or namespace) is preferred than prefixing. However, when we use both classes from Android API and Scaloid, using package name is more verbose than prefixing class name itself (compare with `common.Button` and `$Button`) and can be confused when you use both classes at the same code. We choose pragmatism rather than discipline.</sub>

## Classes

### Class AlertDialogBuilder

A Scala-style builder for AlertDialog.

    new AlertDialogBuilder(R.string.title, R.string.message) {
      neutralButton()
    }.show()
	
This displays an alert dialog with given string resources. We provide an equivalent shortcut:

    alert(R.string.title, R.string.messag)	
	
Also you can build a more complex dialog:
	
    new AlertDialogBuilder("Exit the app", "Do you really want to exit?") {
      positiveButton("Exit", finishTheApplication())
      negativeButton("Cancel")
    }.show()

The code above is equivalent to:

    new AlertDialog.Builder(context)
      .setTitle("Exit the app")
      .setMessage("Do you really want to exit?")
      .setPositiveButton("Exit", new DialogInterface.OnClickListener {
        def onClick(dialog: DialogInterface, which: Int) {
          finishTheApplication()
        }
      })
      .setNegativeButton("Cancel", new DialogInterface.OnClickListener {
        def onClick(dialog: DialogInterface, which: Int) {
          dialog.cancel()
        }
      }).show()	
	
## Static fields on protected interfaces

Android API has some protected interfaces which has static fields, and inherited it in public classes. For example `android.provider.ContactsContract.Contacts` inherits a protected interface `android.provider.ContactsContract.ContactsColumns`, which defines a static field `ContactsColumns.DISPLAY_NAME`. In Java code, you can access it with `Contacts.DISPLAY_NAME`. However, Scala does not support accessing in this way (please refer [this](https://issues.scala-lang.org/browse/SI-1806) and [this](http://www.scala-lang.org/faq/4)). It is a bad news for Android-Scala programmer. So we provide a workaround implementation for this problem. Just copy-and-paste `Workaround.java` and declare `import org.scaloid.Workarounds._`. Then you can use the interfaces publicly which is originally defined as protected.

	
## Import it to your project

For now, Scaloid is a single-file project. Just copy `common.scala` and paste it to your project and declare `import org.scaloid.common._`. Enjoy!

 * This project can be built with Android API level 8 or higher, and Scala version 2.9.1 or higher.

## Let's make it together!

This project is in early stages, and I will grow it constantly. If you have any idea to improve Scaloid, feel free to open issues or post patches.

### License

This software is licensed under the [Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0.html).


## Roadmap

* **Cover full Android API versions** <br/>
  Currently, only API level 8 is supported. Scaloid may not be compiled with below that API level, and new features introduced above that level are not covered.
  Some of the features to be covered are:
  1. Fragment
  1. New system services
  1. Action bar
  
* **Completely implement Scala getters and setters** <br/>
  A few accessors are currently ported into the Scala style. There are tons of setters in Android API, covering all of them is a hard working. I examined http://scalamacros.org but it seems not good fit on this problem yet. If you have any good idea, please let me know.

* **Build an example Android app** <br/>
  Finish a [scala port of apidemos app](https://github.com/pocorall/scaloid-apidemos) and try another.
* **Build a dedicated website**
* **Write a complete API document**
* **Write the Beginner's guide**
* **Cover full listener shortcuts** <br/>
  Such as EditTextPreference.setOnPreferenceChangeListener
* **Cover openGL ES**
* **Automatically unregister SensorEventListener onStop()**
* **Support Google services** <br/>
  including Google Cloud Messaging (GCM)
* **Write a converter that XML layout into a Scaloid code** <br/>
  First version of the converter would be a simple web application. Providing this functionality as an Eclipse or Intellij plugin would also be great.
  