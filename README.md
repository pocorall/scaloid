# Less painful Android development with Scala

Scala is cool. Writing Android application with Scala is also a cool idea. Because Android exposes Java API, we need some wrapper and utility library to leverage full power of Scala. android-scala-common is an initial attempt to provide this.

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
   Read later part of this document to see how android-scala-common greatly improve your code.
 * **Simple to use**<br/> 
   This is a single-file project. Just copy-and-paste into your project!
 * **Compatible with Android API**<br/>
   You can use both style of android-scala-common and Java Android API. You can gradually improve your legacy code.
 * **Maintained actively**<br/>
   This project is originally created to be used for my own Android app. The first principle of this project is "working right".
   
## Implicit conversions
This library employs several implicit conversions. Some of available implicit conversions are shown below:

    String => Uri
	Int => CharSequence
	Int => Drawable
	( => Any) => OnClickListener
	( => Boolean) => OnLongClickListener
	((View, Boolean) => Any) => OnFocusChangeListener
	( => Boolean) => OnEditorActionListener
	( => Boolean) => OnKeyListener
	((CharSequence, Int, Int, Int) => Any) => TextWatcher
	( => Any) => Runnable
	
There are more implicit conversions available. Check the source code as needed.
	
**Class RichView**

This library defines an implicit conversion `View => RichView`. `RichView` defines additional method for more convenient access to the `View`. For example:

    find[Button](R.id.search).onClick(openUri("http://google.com"))

All of listener-appending methods such as `onKey()`, `onLongClick()`, and `onTouch()` are defined in `RichView`. Some conventions we employed are:

 * We omit `set...`, `add...`, and `...Listener` from the method name, which is less significant.
 * Every methods has two versions of parameters overriden. One is a lazy parameter, and another is a function which has a full parameter defined in original Android API. For example, these two usages are valid:

```
button.onTouch(info("touched"))
button.onTouch((v:View, e:MotionEvent) => info("touched a button "+v))
```	

 * Methods `add...` is abbreviated with function `+=` if it is not a listener-appender.
	
**Class RichTextView**

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
	
**Other 'Rich' classes**

Other `Rich...` classes are also defined to provide additional functionality by implicit conversion. Please check the source code for details.
	
Note: providing shortened listener-appenders and scala style getters/setters are not complete for now. Please refer to our [roadmap](#roadmap).
	
## Context as an implicit parameter
Many methods in Android API requires an instance of a class `Context`. Providing this for every method calls results a clumsy code. We employs an implicit parameter to elliminate this. Just declare an implicit value that represents current context:

    implicit val context = ...

or just extend trait `ContextUtil`, which defines it for you. Then the codes that required `Context` becomes much simpler, for example:


**Intent**

    new Intent(context, classOf[MyActivity])

is reduced to:

    $Intent[MyActivity]

**Starting and stopping service**

    startService(new Intent(context, classOf[MyService]))
    stopService(new Intent(context, classOf[MyService]))	

is reduced to:

    startService[MyService]
    stopService[MyService]
	

**Toast**
    
    Toast.makeText(context, "hi, there!", Toast.LENGTH_SHORT).show()

is reduced to:

    toast("hi, there!")
   
**Dialog**

    ProgressDialog.show(context, "Dialog", "working...", true)

is reduced to:

    spinnerDialog("Dialog", "working...")

**Pending intent**

    PendingIntent.getActivity(context, 0, new Intent(context, classOf[MyActivity]), 0)
    PendingIntent.getService(context, 0, new Intent(context, classOf[MyService]), 0)

is reduced to:

    pendingActivity[MyActivity]
    pendingService[MyService]

**DefaultSharedPreferences**

    PreferenceManager.getDefaultSharedPreferences(context)

is reduced to:

    defaultSharedPreferences


**Play ringtones**

Just play the default notification ringtone:

    play()
	
, specify ringtone resources as a `String`:

    play("content://media/internal/audio/media/50")
	
, or specify a resource `Uri`:
	
	play(alarmSound)

**View resources**

This opens a web browser (or another view assigned to `http` protocol).

   	openUri("http://google.com")

	
## Traits

### Trait ContextUtil

Trait `ContextUtil` includes several shortcuts for frequently used android idioms.

**System services**

Getting system service objects become much simpler.

```
val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
notificationManager.notify(R.string.someString, notification)
val vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
vibrator.vibrate(500)
```

is reduced to:

    notificationManager.notify(R.string.someString, notification)
    vibrator.vibrate(500)

All of the system service accessors available in Android API level 10 are defined.

	
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

Using this, an asynchronous job can be run like this: 

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

Note: Currently, this feature is not supported completely. Check our [roadmap](#roadmap).


### Return value of setters

Setters return the object itself. This feature can be used as a syntactic sugar when a function returning some object. For example, the Java code shown below:

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView textView = getGenericView();
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

is reduced to:

    def getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View, parent: ViewGroup): View =
      getGenericView.text = getGroup(groupPosition).toString

<sub>**Design considerations on returning values:** In C or Java, assinment operator `=` returns a right hand side object. However, chaining assignment operator is very rarelly used in these languages. Assigning the same value to multiple variables might means that your code is badly designed (except some context such as involving intensive mathematical computations). In Scala DSLs, chaining setters are more frequent. For example:</sub>

    getGenericView text_= getGroup(groupPosition).toString maxHeight_= 10
	
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

    $Button("title", onClickBehavior())
	$Intent[MyActivity]

<sub>**Design considerations on making $-ed classes:** In modern programming language, using package (or namespace) is preferred than prefixing. However, when we use both classes from Android API and android-scala-common, using package name is more verbose than prefixing class name itself (compare with `common.Button` and `$Button`) and can be confused when you use both classes at the same code. We choose pragmatism rather than discipline.</sub>

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
	
## Import it to your project

For now, android-scala-common is a single-file project. Just copy `common.scala` and paste it to your project and declare `import net.pocorall.android.common._`. Enjoy!

 * This project can be built with Android API level 10 or higher, and Scala version 2.9.1 or higher.

## Let's make it together!

This project is in early stages, and I will grow it constantly. If you have any idea to improve this library, feel free to open issues or post patches.

### License

This software is licensed under the [Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0.html).

### Recommended resources

* To set up a Maven project that build Android app written in Scala, please refer https://github.com/rohansingh/android-scala-test.

* A delicate problem related to implicit conversion for mult-line block is discussed at  http://stackoverflow.com/questions/12774840/scala-passing-function-parameter-which-does-not-have-parameter.

* If you want to see an example of using this library, check a [scala port of apidemos app](https://github.com/pocorall/android-apidemos-scala).


## Roadmap

* **Cover full Android API versions** <br/>
  Currently, only API level 10 is supported. This library may not be compiled with below that API level, and new features introduced above that level are not covered.
  Some of the features to be covered are:
  1. Fragment
  1. New system services
  1. Action bar
  
* **Completely implement Scala getters and setters** <br/>
  A few accessors are currently ported into the Scala style.

* **Build an example Android app** <br/>
  Finish [scala port of apidemos app](https://github.com/pocorall/android-apidemos-scala) and try another.
* **Build a dedicated website**
* **Write a complete API document**
* **Write the Beginner's guide**
* **Cover full listener shortcuts** <br/>
  Such as EditTextPreference.setOnPreferenceChangeListener
* **Cover openGL ES**
* **Automatically unregister SensorEventListener onStop()**
* **Support Google services** <br/>
  including Google Cloud Messaging (GCM)