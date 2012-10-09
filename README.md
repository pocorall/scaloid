# android-scala-common

Scala is cool. Writing Android application with Scala is also a cool idea. Because Android exposes Java API, we need some wrapper and utility library to leverage full power of Scala. android-scala-common is an initial attempt to provide this.

## Implicit conversions
This library employs several implicit conversions. 

```
helpButton.setOnClickListener {
  new OnClickListener {
	def onClick(dialog: DialogInterface, which: Int) {
	  Log.i("pocorall", "pressed!")
	}
  }
}
```

is reduced to:

```
helpButton.setOnClickListener(Log.i("pocorall", "pressed!"))
```

For a multi-line block:

```
helpButton.setOnClickListener {
  (v: View) =>
    Log.i("pocorall", "pressed!")
    Log.i("pocorall", v.toString())
}
```

## Traits

### ContextUtil

Trait ContextUtil includes several shortcuts for frequently used android idioms.

**System services**

Getting system service objects become much simpler. Instead of:

```
val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
notificationManager.notify(R.string.someString, notification)
val vibrator = getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
vibrator.vibrate(500)
```

, use like this:

```
notificationManager.notify(R.string.someString, notification)
vibrator.vibrate(500)
```

### RunOnUiThread

Provides Scala version of runOnUiThread() implementation. You can use it anywhere other than class Activity.

Instead of:

```
runOnUiThread {
  new Runnable() {
	def run() {
	  Log.i("I am running", "only for Activity class")
	}
  }
}
```

, extend trait RunOnUiThread and use it like this:

```
runOnUiThread(Log.i("I am running", "for any context"))
```

### UnregisterReceiverService

When you registered BroadcastReceiver with Context.registerReceiver() you have to unregister it to prevent memory leak. Trait UnregisterReceiverService handles these chores for you by just extend it for your Service.

```
class MyService extends Service with UnregisterReceiverService {
  def func() {
    // ...
	registerReceiver(receiver, intentFilter)
	// Done! automatically unregistered at onDestroy()
  }
}
```

## Import it to your project

For now, android-scala-common is a single-file project. Just copy ScalaUtils.scala and paste it to your project. Enjoy!

## Let's make it together!

This project is very early stage, and I will grow it constantly. If you have any idea to improve this library, feel free to open issues or post patches.

### License

This software is licensed under the Apache 2 license.

### Recommended resources

* To set up a Maven project that build Android app written in Scala, please refer https://github.com/rohansingh/android-scala-test.

* A delicate problem related to implicit conversion for mult-line block is discussed at  http://stackoverflow.com/questions/12774840/scala-passing-function-parameter-which-does-not-have-parameter.
