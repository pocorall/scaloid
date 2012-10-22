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
	
	layout.addView(newButton("send", sendMessage()))


### Benefits
 * **Write elegant Android software**<br/>
   Read later part of this document to see how android-scala-common greatly improve your code.
 * **Simple to use**<br/> 
   This is a single-file project. Just copy-and-paste into your project!
 * **Maintained actively**<br/>
   This project is originally created to be used for my own Android app. The first principle of this project is "working right".

## Implicit conversions
This library employs several implicit conversions. A code block:

```
button.setOnClickListener(new OnClickListener {
  def onClick(v:View) {
    Log.i("pocorall", "pressed!")
  }
})
```

is reduced to:

    button.setOnClickListener(Log.i("pocorall", "pressed!"))

For a multi-line block:

```
button.setOnClickListener {
  (v: View) =>
    Log.i("pocorall", "pressed!")
    Log.i("pocorall", v.toString())
}
```

Some of available implicit conversions are shown below:

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
	

## Context as an implicit parameter
Many methods in Android API requires an instance of a class `Context`. Providing this for every method calls results a clumsy code. We employs implicit parameter to elliminate this. Just declare an implicit value that represents current context:

    implicit val context = ...

or just extend trait `ContextUtil`, which defines it for you. Then the codes that required `Context`, for example:

```
new Intent(context, classOf[MyActivity])
startService(new Intent(context, classOf[MyService]))
Toast.makeText(context, "hi, there!", Toast.LENGTH_SHORT).show()
ProgressDialog.show(context, "Dialog", "working...", true)
PendingIntent.getActivity(context, 0, new Intent(context, classOf[MyActivity]), 0)
PendingIntent.getService(context, 0, new Intent(context, classOf[MyService]), 0)
PreferenceManager.getDefaultSharedPreferences(context)
```

is reduced to:

```
newIntent[MyActivity]
startService[MyService]
toast("hi, there!")
spinnerDialog("Dialog", "working...")
pendingActivity[MyActivity]
pendingService[MyService]
defaultSharedPreferences
```

   
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

```
notificationManager.notify(R.string.someString, notification)
vibrator.vibrate(500)
```

All of the system service accessors described in Android API level 10 are defined.

**Play ringtones**

Just play default notification ringtone:

    play()
	
, specify ringtone resources as `String`:

    play("content://media/internal/audio/media/50")
	
, or specify resource `Uri`s:
	
	play(alarmSound)

	
### Trait RunOnUiThread

Android API provides `runOnUiThread()` only for class `Activity`. Trait `RunOnUiThread` provides Scala version of `runOnUiThread()` for anywhere other than `Activity`.

Instead of:

```
activity.runOnUiThread {
  new Runnable() {
	def run() {
	  Log.i("I am running", "only for Activity class")
	}
  }
}
```

, extend trait `RunOnUiThread` and use it like this:

    runOnUiThread(Log.i("I am running", "for any context"))


### Trait UnregisterReceiverService

When you registered `BroadcastReceiver` with `Context.registerReceiver()` you have to unregister it to prevent memory leak. Trait UnregisterReceiverService handles these chores for you by just extend it for your Service.

```
class MyService extends Service with UnregisterReceiverService {
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

or, extend trait `ContextUtil` which defines this by default. Then you can simply log like this:

	info("hello " + world)

Other functions for every log levels(verbose, debug, info, warn, error, wtf) are available. A `String` parameter passed with `info()` is a lazy argument, and evaluated only if the logging is possible. Therefore the example shown above is equivalent to:
	
	val tag = "MyAppTag"
	if(Log.isLoggable(tag, Log.INFO)) Log.i(tag, "hello " + world)

	
## Classes

### Class AlertDialogBuilder

A Scala-style builder for AlertDialog.

    new AlertDialogBuilder("Exit the app", "Do you really want to exit?")
      .positiveButton("Exit", (_, _) => {
          // cleanup the application
          finish()
        })
      .negativeButton("Cancel")
      .show()

## Import it to your project

For now, android-scala-common is a single-file project. Just copy `common.scala` and paste it to your project and `import net.pocorall.android.common._`. Enjoy!

 * This project can be built with Android API level 10 or higher, and Scala version 2.9.1 or higher.

## Let's make it together!

This project is in early stages, and I will grow it constantly. If you have any idea to improve this library, feel free to open issues or post patches.

### License

This software is licensed under the [Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0.html).

### Recommended resources

* To set up a Maven project that build Android app written in Scala, please refer https://github.com/rohansingh/android-scala-test.

* A delicate problem related to implicit conversion for mult-line block is discussed at  http://stackoverflow.com/questions/12774840/scala-passing-function-parameter-which-does-not-have-parameter.
