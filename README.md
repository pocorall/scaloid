<!---
/*
 *
 *
 *
 * Scaloid: Simpler Android
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee and Scaloid contributors
 *
 * Sung-Ho Lee licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
-->
<p align="center"><img src="http://o-n2.com/scaloid_logo.png"></p>

# Simpler Android

Scaloid is a library that simplifies your Android code. It makes your code easy to understand and maintain by [leveraging Scala language](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-faqs-about-scaloid).

For example, the code block shown below:

```scala
val button = new Button(context)
button.setText("Greet")
button.setOnClickListener(new OnClickListener() {
  def onClick(v: View) {
    Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
  }
})
layout.addView(button)
```

is reduced to:

```scala
SButton("Greet", toast("Hello!"))
```


### Benefits
 * **Write elegant Android software**<br/>
   Simplicity is number one principle, keeps programmability and type-safety.
 * **Easy to use**<br/>
   Check the [quick start guide](https://github.com/pocorall/scaloid/wiki/Installation#wiki-quick-start)
 * **Compatible with your legacy code**<br/>
   You can [use both Scaloid and plain-old Java Android API](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-i-cant-use-scaloid-because-it-does-not-provide-a-functionality-x). You can gradually improve your legacy code.
 * **Production quality**<br/>
   Not a toy project. The creator of Scaloid uses it to build [a millionth downloaded app](https://play.google.com/store/apps/details?id=com.soundcorset.client.android).

### Demos

Fork one of this to start a new project:
 * [<b>Hello world of Scaloid for sbt</b>](https://github.com/pocorall/hello-scaloid-sbt) (recommended, it builds faster)
 * [<b>Hello world of Scaloid for maven</b>](https://github.com/pocorall/hello-scaloid-maven)
 * [<b>Hello world of Scaloid for gradle</b>](https://github.com/pocorall/hello-scaloid-gradle)

Learn how Scaloid can be used in action:
 * [<b>Scaloid port of apidemos app</b>](https://github.com/pocorall/scaloid-apidemos)
 * [<b>List of projects using Scaloid</b>](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-list-of-projects-using-scaloid)
 * [<b>Tutorial by Gaston Hillar</b>](http://www.drdobbs.com/mobile/developing-android-apps-with-scala-and-s/240161584) - [part 1](http://www.drdobbs.com/mobile/developing-android-apps-with-scala-and-s/240161584) and [part 2](http://www.drdobbs.com/mobile/developing-android-apps-with-scala-and-s/240162204)


## Contents

 * [Core design principle](#core-design-principle)
 * [UI Layout without XML](#ui-layout-without-xml)
   * [Layout context](#layout-context)
   * [Styles for programmers](#styles-for-programmers)
   * [Automatic layout converter](#automatic-layout-converter)
 * [Lifecycle management](#lifecycle-management)
 * [Asynchronous task processing](https://github.com/pocorall/scaloid/wiki/Basics#wiki-asynchronous-task-processing)
 * [Implicit conversions](https://github.com/pocorall/scaloid/wiki/Basics#wiki-implicit-conversions)
   * [Shorter listeners](https://github.com/pocorall/scaloid/wiki/Basics#wiki-enriched-implicit-classes)
   * [Database cursor](http://blog.scaloid.org/2014/02/simple-enhancements-on-accessing.html)
 * [Traits](https://github.com/pocorall/scaloid/wiki/Basics#wiki-traits)
 * [Smarter logging](https://github.com/pocorall/scaloid/wiki/Basics#wiki-logging)
 * [Improved getters/setters](#scala-getters-and-setters)
 * [Classes](#classes)
   * [Concise dialog builder](#class-alertdialogbuilder)
   * [Beauty ArrayAdapter](#class-sarrayadapter)
   * [Dynamically Preferences](#class-preferences)  [<sub>`Read in blog`</sub>](http://blog.scaloid.org/2013/03/dynamicly-accessing-sharedpreferences.html)
   * [Binding services concisely](#class-localservice)  [<sub>`Read in blog`</sub>](http://blog.scaloid.org/2013/03/introducing-localservice.html)
   
## Other links   
 * [<b>Quick start guide</b>](https://github.com/pocorall/scaloid/wiki/Installation#wiki-quick-start)
 * [<b>API doc</b>](http://docs.scaloid.org/)
 * [<b>Blog</b>](http://blog.scaloid.org/)
 * [<b>Twitter</b>](https://twitter.com/scaloid/)
 * [<b>FAQs</b>](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-faqs-about-scaloid)
     * [FAQs about Scala on Android](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-faqs-about-scala-on-android)
 * [<b>Inside Scaloid</b>](https://github.com/pocorall/scaloid/wiki/Inside-Scaloid)
 * [<b>We are hiring!</b>](#we-are-hiring)

## Core design principle

"Being practically simple" is number one principle of Scaloid. Most frequently used things should be written shorter, like [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding). To do this, I first observed Android programs I wrote, and thought that which part of the code is more fundamental than others. For example, what is the most essential part of buttons? Buttons should have some visible things on it, such as title or image, so the buttons are created like this: `SButton("Hello")`. The second essential part is doing something when it is pressed: `SImageButton(R.drawable.hello, toast("World!"))`. What should be the third one? The answer might not the same for every people, but I think that repetition frequency of press-and-hold action is nice: `SButton("Add", n += 1, 500)` increases `n` for every 500 milliseconds when the user holds the button.

## UI Layout without XML
<p align="center"><img src="http://o-n2.com/verboseSimple.png"></p>

Android SDK leverages XML to build UI layouts. However, XML is considered still a bit verbose, and lacks programmability. Scaloid composes UI layout in Scala DSL style, therefore achieve both clarity and programmability. For example, suppose a legacy XML layout as shown below:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical" android:layout_width="match_parent"
        android:layout_height="wrap_content" android:padding="20dip">
    <TextView android:layout_width="match_parent"
            android:layout_height="wrap_content" android:text="Sign in"
            android:layout_marginBottom="25dip" android:textSize="24.5sp"/>
    <TextView android:layout_width="match_parent"
            android:layout_height="wrap_content" android:text="ID"/>
    <EditText android:layout_width="match_parent"
            android:layout_height="wrap_content" android:id="@+id/userId"/>
    <TextView android:layout_width="match_parent"
            android:layout_height="wrap_content" android:text="Password"/>
    <EditText android:layout_width="match_parent"
            android:layout_height="wrap_content" android:id="@+id/password"
            android:inputType="textPassword"/>
    <Button android:layout_width="match_parent"
            android:layout_height="wrap_content" android:id="@+id/signin"
            android:text="Sign in"/>
    <LinearLayout android:orientation="horizontal"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">
        <Button android:text="Help" android:id="@+id/help"
                android:layout_width="match_parent" android:layout_height="wrap_content"/>
        <Button android:text="Sign up" android:id="@+id/signup"
                android:layout_width="match_parent" android:layout_height="wrap_content"/>
    </LinearLayout>
</LinearLayout>
```

is reduced to:

```scala
new SVerticalLayout {
  STextView("Sign in").textSize(24.5.sp).<<.marginBottom(25.dip).>>
  STextView("ID")
  SEditText()
  STextView("Password")
  SEditText() inputType TEXT_PASSWORD
  SButton("Sign in")
  new SLinearLayout {
    SButton("Help")
    SButton("Sign up")
  }.wrap.here
}.padding(20.dip)
```

The layout description shown above is highly programmable. You can easily wire your logic into the layout:

```scala
new SVerticalLayout {
  STextView("Sign in").textSize(24.5.sp).<<.marginBottom(25.dip).>>
  STextView("ID")
  val userId = SEditText()
  STextView("Password")
  val pass = SEditText() inputType TEXT_PASSWORD
  SButton("Sign in", signin(userId.text, pass.text))
  new SLinearLayout {
    SButton("Help", openUri("http://help.url"))
    SButton("Sign up", openUri("http://signup.uri"))
  }.wrap.here
}.padding(20.dip)
```

Because a Scaloid layout description is plain Scala code, it is type-safe.

### Automatic layout converter

This converter turns an Android XML layout into a Scaloid layout:

http://layout.scaloid.org

### Migration tip 

Scaloid is fully compatible with legacy xml layout files. 
You can access a widget described in xml layout as:

```scala
onCreate {
  setContentView(R.layout.main)
  val name = find[EditText](R.id.name)
  // do something with `name`
}
```

### Responsive layout

Basically, a layout written in Scaloid is just an ordinary Scala code, so you can just freely composite the layout according to the device configuration:

```scala
import org.scaloid.util.Configuration._

if(long) SButton("This button is shown only for a long screen "
  + "dimension ("+ width + ", " + height + ")")
if(landscape) new SLinearLayout {
  SButton("Buttons for")
  SButton("landscape layout")
  if(dpi <= HDPI) SButton("You have a high resolution display!")
}.here
```

Please refer to this blog post for more detail:
 - [Syntactic sugar for multiple device configuration](http://blog.scaloid.org/2013/08/syntactic-sugar-for-multiple-device.html)

### Further readings about Scaloid layout 

 - [Accessing widgets in view class](http://blog.scaloid.org/2013/04/accessing-widgets-in-view-classes.html)
 - [Layout context](#layout-context)
 - [In-depth tutorial on styles](http://blog.scaloid.org/2013/01/a-css-like-styling-on-android.html)
 - [Styles for programmers](#styles-for-programmers)

## Lifecycle management

With Android API, Registering and unregistering BroadcastReceiver can be done as:

```scala
var connectivityListener: BroadcastReceiver = null

def onResume() {
  super.onResume()
  // ...
  connectivityListener = new BroadcastReceiver {
    def onReceive(context: Context, intent: Intent) {
     doSomething()
    }
  } 
  registerReceiver(connectivityListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
}

def onPause() {
  unregisterReceiver(connectivityListener)
  // ...
  super.onPause()
}
```

In Scaloid, the directly equivalent code is:

```scala
broadcastReceiver(ConnectivityManager.CONNECTIVITY_ACTION) {
  doSomething()
}
```

Scaloid has highly flexible resource register/unregister management architecture.
If this code is written in services, registering and unregistering is done in onCreate and onDestroy respectively. 
If the same code is in activities, registering and unregistering is done in onResume and onPause respectively.
This is just a default behavior. 
You can override a preference that determine when the register/unregister preforms. 
Overriding it is simple as well:

```scala
broadcastReceiver(ConnectivityManager.CONNECTIVITY_ACTION) {
  doSomething()
}(this, onStartStop)
```

Then, the receiver is registered onStart, and unregistered onStop.

#### onDestroy can be called in many times!

You can declare `onDestroy` behaviors in many places. This simplifies resource management significantly. Suppose you want to open a stream from a file: 

```scala
def openFileStream(file: File): InputStream = {
  val stream = new FileInputStream(file)
  onDestroy(stream.close()) // automatically closed when the Activity is destroyed!!
  stream
}
```

`onDestroy` is a method that adds a function into the job list triggered when the activity is destroyed. So, we just get a stream from `openFileStream()` and forget about releasing it.
Other lifecycle states (`onCreate`, `onResume`, `onStop` and so on) can be treated in the same way.

**Further reading:** Refer to [this blog post](http://blog.scaloid.org/2013/02/better-resource-releasing-in-android.html) for more details.
 
 
## Asynchronous task processing

Android API provides `runOnUiThread()` only for class `Activity`. Scaloid provides a Scala version of `runOnUiThread()` for anywhere other than `Activity`.

Instead of:

```scala
activity.runOnUiThread {
  new Runnable() {
    def run() {
      debug("Running only in Activity class")
    }
  }
}
```

In Scaloid, use it like this:

```scala
runOnUiThread(debug("Running in any context"))
```

Running a job asynchronously and notifying the UI thread is a very frequently used pattern. Although Android API provides a helper class `AsyncTask`, implementing such a simple idea is still painful, even when we use Scala:

```scala
new AsyncTask[String, Void, String] {
  def doInBackground(params: Array[String]) = {
    doAJobTakeSomeTime(params)
  }

  override def onPostExecute(result: String) {
    alert("Done!", result)
  }
}.execute("param")
```

Using [`scala.concurrent.Future`](http://docs.scala-lang.org/sips/completed/futures-promises.html), the asynchronous job shown above can be rewritten like this:

```scala
Future {
  val result = doAJobTakeSomeTime(params)
  runOnUiThread(alert("Done!", result))
}
```

When you don't want to build sophisticate UI interactions, but just want to display something by calling a single Scaloid method (e.g. `alert`, `toast`, and `spinnerDialog`), Scaloid handles `runOnUiThread` for you. Therefore, the code block shown above is reduced to:

```scala
Future {
  alert("Done!", doAJobTakeSomeTime(params))
}
```

It is a great win as it exposes your idea clearly.

Just like we thrown away `AsyncTask`, we can also eliminate all other Java helpers for asynchronous job, such as `AsyncQueryHandler` and `AsyncTaskLoader`. Compare with the [original Java code](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/view/ExpandableList2.java?av=h)
and a [Scala port](https://github.com/pocorall/scaloid-apidemos/blob/master/src/main/java/com/example/android/apis/view/ExpandableList2.scala) of ApiDemos example app.

Using `Future` is just an example of asynchronous task processing in Scaloid. You can freely use any modern task management utilities.

**Further reading:** Refer to [this blog post](http://blog.scaloid.org/2013/11/using-scalaconcurrentfuture-in-android.html) for an important consideration when using `Future` in Android.


## Implicit conversions
Scaloid employs several implicit conversions. Some of the available implicit conversions are shown below:

##### Uri conversion

```scala
String => Uri
```

The functions such as [play ringtones](#play-ringtones) `play()` or [open URIs](#open-uris) `openUri()` takes an instance of `Uri` as a parameter. However, we frequently have URIs as a `String`. Scaloid implicitly converts `String` into `Uri`. Therefore, you can freely use `String` when you play a ringtone:

```scala
play("content://media/internal/audio/media/50")
```

, open a URI:

```scala
openUri("http://scaloid.org")
```

, or wherever you want.

Alternatively, you can specify the conversion as:

```scala
val uri:Uri = "http://scaloid.org".toUri
```

##### Unit conversion

Units `dip` and `sp` can be converted into the pixel unit.

```scala
val inPixel:Int = 32.dip
val inPixel2:Int = 22.sp
```

Reversely, pixel unit can also be converted into `dip` and `sp` unit.

```scala
val inDip:Double = 35.px2dip
val inSp:Double = 27.px2sp
```

##### Resource IDs

Scaloid provides several implicit conversions that convert from `Int` type resource ID to `CharSequence`, `Array[CharSequence]`, `Array[String]`, `Drawable` and `Movie`.
For example:

```scala
def toast(msg:CharSequence) = ...

toast(R.string.my_message) // implicit conversion works!
```

Although Scaloid provides these conversions implicitly, explicit conversion may be required in some context. In this case, methods `r2...` are provided for the `Int` type:

```scala
warn("Will display the content of the resource: " + R.string.my_message.r2String)
```

Currently, `r2Text`, `r2TextArray`, `r2String`, `r2StringArray`, `r2Drawable` and `r2Movie` is provided.


**Further reading:**
 * [Why implicit conversion of Resource ID is cool?](https://github.com/pocorall/scaloid/wiki/Basics#wiki-why-implicit-conversion-of-resource-id-is-cool)



## Context as an implicit parameter
Many methods in the Android API require an instance of a class `Context`. Providing this for every method call results in clumsy code. We employ an implicit parameter to eliminate this. Just declare an implicit value that represents current context:

```scala
implicit val ctx = ...
```

or just extend trait `SContext`, which defines it for you. Then the code that required `Context` becomes much simpler, for example:


##### Intent

```scala
new Intent(context, classOf[MyActivity])
```

is reduced to:

```scala
SIntent[MyActivity]
```

When a method takes an `Intent` as a first parameter in which we want to pass the newly created intent object, the parameter can be omitted. For example:

```scala
startService(new Intent(context, classOf[MyService]))
stopService(new Intent(context, classOf[MyService]))
```

is reduced to:

```scala
startService[MyService]
stopService[MyService]
```

or

```scala
val intent = // initialize the intent and put some attributes on it
intent.start[MyActivity]
```

An intent that has a long list of extra attributes:

```scala
new Intent().putExtra("valueA", valueA).putExtra("valueB", valueB).putExtra("valueC", valueC)
```

is reduced to:

```scala
new Intent().put(valueA, valueB, valueC)
```

##### Toast

```scala
toast("hi, there!")
```

If you want a longer toast:

```scala
longToast("long toast")
```

##### Dialog

```scala
ProgressDialog.show(context, "Dialog", "working...", true)
```

is reduced to:

```scala
spinnerDialog("Dialog", "working...")
```

When you call `toast`, `longToast` or `spinnerDialog` from non-UI thread, you [don't have to mind about threading](#asynchronous-task-processing).
The toast example shown above is equivalent to the following Java code: 

```java
activity.runOnUiThread(new Runnable() {
    public void run() {
        Toast.makeText(activity, "hi, there!", Toast.LENGTH_SHORT).show();
    }
});
```

##### Pending intent

```scala
PendingIntent.getActivity(context, 0, new Intent(context, classOf[MyActivity]), 0)
PendingIntent.getService(context, 0, new Intent(context, classOf[MyService]), 0)
```

is reduced to:

```scala
pendingActivity[MyActivity]
pendingService[MyService]
```

##### Open URIs

This opens a web browser (or another view assigned to the http protocol).

```scala
openUri("http://scaloid.org")
```


##### System services

Getting system service objects become much simpler. The following legacy code:

```scala
val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
vibrator.vibrate(500)
```

is reduced to:

```scala
vibrator.vibrate(500)
```

Under the hood, Scaloid defines a function `vibrator` like this:

```scala
def vibrator(implicit ctx: Context) = ctx.getSystemService(Context.VIBRATOR_SERVICE).asInstanceOf[Vibrator]
```

All the system service accessors available in Android API level 8 are defined (e.g. `audioManager`, `alarmManager`, `notificationManager`, etc.). The name of a system service accessor is the same as its class name, except that the first character is lowercased.


## Enriched Implicit classes

Suppose an Android class `Foo`, for example, Scaloid defines an implicit conversion `Foo => RichFoo`. The class `RichFoo` defines additional methods for more convenient access to `Foo`. This is a common pattern in Scala to extend existing API (see [pimp-my-library](http://www.artima.com/weblogs/viewpost.jsp?thread=179766) pattern). This section describes various features added on existing Android API classes.


##### Listeners

Android API defines many listener interfaces for callback notifications. For example, `View.OnClickListener` is used to be notified when a view is clicked:

```scala
find[Button](R.id.search).setOnClickListener(new View.OnClickListener {
  def onClick(v:View) {
    openUri("http://scaloid.org")
  }
})
```

Scaloid provides a shortcut that dramatically reduces the length of the code:

```scala
find[Button](R.id.search).onClick(openUri("http://scaloid.org"))
```

All other listener-appending methods such as `.onKey()`, `.onLongClick()`, and `.onTouch()` are defined.

Some conventions we employed for method naming are:

 * We omit `set...`, `add...`, and `...Listener` from the method name, which is less significant.<br/>
   For example, `.setOnKeyListener()` becomes `.onKey()`.
 * Every method has two versions of parameters overridden. One is a lazy parameter, and another is a function which has full parameters defined in the original Android API. For example, these two usages are valid:

```scala
button.onClick(info("touched"))
button.onClick((v:View) => info("touched a button "+v))
```

 * Methods `add...` is abbreviated with a method `+=` if it is not a listener-appender.<br/>
   For example, `layout.addView(button)` becomes `layout += button`.

##### Multiple method listeners

Methods `beforeTextChanged()`, `onTextChanged()`, and `afterTextChanged()` are defined in `RichTextView`, which can be implicitly converted from `TextView`. It is more convenient than using `TextWatcher` directly. For example:

```scala
inputField.beforeTextChanged(saveTextStatus())
```

is equivalent to:

```scala
inputField.addTextChangedListener(new TextWatcher {
  def beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    saveTextStatus()
  }

  def onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

  def afterTextChanged(p1: Editable) {}
})
```

Also, we override `beforeTextChanged()` with full parameters defined in the original listener:

```scala
inputField.beforeTextChanged((s:CharSequence, _:Int, _:Int) => saveText(s))
```

Other listeners in Android API can also be accessed in this way.

## Layout context

In Android API, layout information is stored into a `View` object via the method `View.setLayoutParams(ViewGroup.LayoutParams)`. A specific type of parameter passing into that method is determined by a the type of `...Layout` object which contains the `View` object. For example, let us see some Java code shown below:

```java
LinearLayout layout = new LinearLayout(context);
Button button = new Button(context);
button.setText("Click");
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams();
params.weight = 1.0f;  // sets some value
button.setLayoutParams(params);
layout.addView(button);
```

Because the button is appended into the `LinearLayout`, the layout parameter must be `LinearLayout.LayoutParams`, otherwise a ___runtime error___ might be occurred. Meanwhile, Scaloid eliminate this burden, while still preserving rigorous typing of `LayoutParams`. The code shown below is equivalent to the previous Java code:

```scala
val layout = new SLinearLayout {
  SButton("Click").<<.Weight(1.0f).>>
}
```

In the anonymous constructor of 'SLinearLayout', Scaloid provides an implicit function called "layout context". This affects a return type of the method `<<` defined in the class `SButton`. 
If we use `SFrameLayout` as a layout context, the method `<<` returns `FrameLayout.LayoutParams`, which does not have `Weight` method. Therefore, the code below results a ___syntax error___.

```scala
val layout = new SFrameLayout {
  SButton("Click").<<.Weight(1.0f).>>   // Syntax error on Weight()
}
```

Compared with XML layout description, Scaloid layout is simple and type-safe.

The method `<<` is overloaded with parameters `<<(width:Int, height:Int)` which assigns the size of the view component. For example:

```scala
SButton("Click").<<(40.dip, WRAP_CONTENT)
```

#### Operator `new` and method `apply`

Usually, `View` components are referenced multiple times in an `Activity`. For example:

```scala
lazy val button = new SButton() text "Click"
onCreate {
  contentView = new SLinearLayout {
    button.here
  }
}
// ... uses the button somewhere in other methods (e.g. changing text or adding listeners)
```

[Prefixed classes](https://github.com/pocorall/scaloid/wiki/Basics#wiki-prefixed-classes) in Scaloid (e.g. `SButton`) have a companion object that implements `apply` methods that create a new component. These methods also append the component to the layout context that enclose the component. 
Therefore, the code block from the above example:

```scala
button = new SButton() text "Click"
button.here
```

is equivalent to:

```scala
button = SButton("Click")
```

Because the `apply` methods access to the layout context, it cannot be called outside of the layout context. 
In this case, use the `new` operator instead.

#### Method `>>`

As we noted, the method `<<` returns an object which is a type of `ViewGroup.LayoutParams`:

```scala
val params = SButton("Click").<<   // type LayoutParams
```

This class provides some setters for chaining:

```scala
val params = SButton("Click").<<.marginBottom(100).marginLeft(10)   // type LayoutParams
```

if we want use the `SButton` object again, Scaloid provides `>>` method returning back to the object:

```scala
val button = SButton("Click").<<.marginBottom(100).marginLeft(10).>>   // type SButton
```

#### Nested layout context

When the layout context is nested, inner-most layout's context is applied:

```scala
val layout = new SFrameLayout {
  new SLinearLayout {
    SButton("Click").<<.Weight(1.0f).>>   // in context of SLinearLayout
  }.here
}
```

#### Methods `fill`, `wrap`, `wf` and `fw`

When we get a `LayoutParams` from `<<`, the default values of `width` and `height` properties are `width = FILL_PARENT` and `height = WRAP_CONTENT`. You can override this when you need it:

```scala
SButton("Click").<<(FILL_PARENT, FILL_PARENT)
```

This is a very frequently used idiom. Therefore we provide further shorthand:

```scala
SButton("Click").<<.fill
```

If you want the `View` element to be wrapped,

```scala
SButton("Click").<<(WRAP_CONTENT, WRAP_CONTENT)
```

This is also shortened as:

```scala
SButton("Click").<<.wrap
```

Similarly, `<<(WRAP_CONTENT, FILL_PARENT)` and `<<(FILL_PARENT, WRAP_CONTENT)` can also be shortend as `<<.wf` and `<<.fw` respectively.

Because there are so many occurences `<<.wrap.>>` pattern in actual Android code, it is allowed to remove `.<<` and `.>>` in this case:

```scala
SButton("Click").wrap    // returns SButton type
```

This pattern also usable for `.fill`, `.fw` and `.wf` methods.


## Styles for programmers

#### Naming conventions

Scaloid follows the naming conventions of XML attributes in the Android API with some improvements.

For XML attributes, layout related properties are prefixed with `layout_` and as you might have guessed, Scaloid does not need it. 
For boolean attributes, the default is `false`. However, Scaloid flags it as `true` when the attribute is declared explicitly without any parameter. 
For example:

```scala
new SRelativeLayout {
  STextView("hello").<<.centerHorizontal.alignParentBottom.>>
}
```

Scaloid omits unnecessary `="true"` for the attribute `centerHorizontal`. Equivalent XML layout description for `TextView` is:

```xml
<TextView
    android:id="@+id/helloText"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:layout_centerHorizontal="true"
    android:layout_alignParentBottom="true"
    android:text="hello"/>
```

For layout methods named with four directions (e.g. `...Top`, `...Right`, `...Bottom` and `...Left`), Scaloid provides additional methods that specifies all properties at once. For example, Because Android XML layout defines `margin...` properties(`marginTop(v:Int)`, `marginRight(v:Int)`, `marginBottom(v:Int)` and `marginLeft(v:Int)`), Scaloid provides additional `margin(top:Int, right:Int, bottom:Int, left:Int)` and `margin(amount:Int)` methods that can be used as:

```scala
STextView("hello").<<.margin(5.dip, 10.dip, 5.dip, 10.dip)
```

or

```scala
STextView("hello").<<.margin(10.sp)  // assigns the same value for all directions
```


Android SDK introduced [styles](http://developer.android.com/guide/topics/ui/themes.html) to reuse common properties on XML layout.
We repeatedly pointed out that XML is verbose.
To apply styles in Scaloid, you do not need to learn any syntax or API library, because Scaloid layout is an ordinary Scala code. Just write a code that work as styles.

#### Basic: Assign it individually

Suppose the following code that repeats some properties:

```scala
SButton("first").textSize(20.dip).<<.margin(5.dip).>>
SButton("prev").textSize(20.dip).<<.margin(5.dip).>>
SButton("next").textSize(20.dip).<<.margin(5.dip).>>
SButton("last").textSize(20.dip).<<.margin(5.dip).>>
```

Then we can define a function that applies these properties:

```scala
def myStyle = (_: SButton).textSize(20.dip).<<.margin(5.dip).>>
myStyle(SButton("first"))
myStyle(SButton("prev"))
myStyle(SButton("next"))
myStyle(SButton("last"))
```

Still not satisfying? Here we have a shorter one:

```scala
def myStyle = (_: SButton).textSize(20.dip).<<.margin(5.dip).>>
List("first", "prev", "next", "last").foreach(title => myStyle(SButton(title)))
```

#### Advanced: CSS-like stylesheet

Scaloid provides `SViewGroup.style(View => View)` method to provide more generic component styling. 
The parameter is a function which receives a view requested for styling, and returns a view which is finished applying the style.
Then the example in the previous subsection becomes:

```scala
style {
  case b: SButton => b.textSize(20.dip).<<.margin(5.dip).>>
}

SButton("first")
SButton("prev")
SButton("next")
SButton("last")
```

Note that individually applying `myStyle` is reduced. Let us see another example:

```scala
style {
  case b: SButton => b.textColor(Color.RED).onClick(toast("Bang!"))
  case t: STextView => t.textSize(10.dip)
  case v => v.backgroundColor(Color.YELLOW)
}

STextView("I am 10.dip tall")
STextView("Me too")
STextView("I am taller than you").textSize(15.dip) // overriding
SEditText("Yellow input field")
SButton("Red alert!")
```
  
Similar to CSS, you can assign different styles for each classes using Scala pattern matching. 
Unlike Android XML styles or even CSS, Scaloid can assign some actions to the component (see `onclick(toast(...))`), or can do anything that you imagine. 
Also, you can easily override the property individually, as shown in the example above.

Last thing that you may missed: These are type-safe. If you made a mistake, compiler will check it for you.

**Further readings:**

 - [Accessing widgets in view class](http://blog.scaloid.org/2013/04/accessing-widgets-in-view-classes.html)
 - [In-depth tutorial on styles](http://blog.scaloid.org/2013/01/a-css-like-styling-on-android.html)
  
## Traits

### Trait `UnregisterReceiver`

When you register `BroadcastReceiver` with `Context.registerReceiver()` you have to unregister it to prevent memory leak. Trait `UnregisterReceiver` handles these chores for you. All you need to do is append the trait to your class.

```scala
class MyService extends SService with UnregisterReceiver {
  def func() {
    // ...
    registerReceiver(receiver, intentFilter)
    // Done! automatically unregistered at UnregisterReceiverService.onDestroy()
  }
}
```

### Trait `SActivity`

Instead of

```scala
findViewById(R.id.login).asInstanceOf[Button]
```

use a shorthand:

```scala
find[Button](R.id.login)
```

Although we provide this shorthand, Scaloid recommends [programmatically laying out UI, not with XML](https://github.com/pocorall/scaloid/wiki/UI-Layout-without-XML).

## Activity as an implicit parameter
Similar to the [implicit context](#context-as-an-implicit-parameter), an `Activity` typed implicit parameter is also required for some methods. Therefore, you have to define an activity as an implicit value:

```scala
implicit val ctx: Activity = ...
```

Because the class `Activity` is a subclass of `Context`, it can also be an implicit context.
When you extend `SActivity`, object `this` is assigned as the implicit activity by default.

Here we show some example cases of using the implicit activity:

#### Automatically allocate a unique `View` ID

Often, `Views` are required to have an ID value. Although Android API document specifies that the ID need not be unique, allocating unique ID is virtually mandatory in practice. Scaloid provides a package scope function `getUniqueId`, which returns `Int` type ID that is not allocated by any existing `View` components for given implicit activity.

```scala
val newUniqueIdForCurrentActivity = getUniqueId
```

Using this, Scaloid also extended `View` class to add a method `uniqueId`, that assigns a new unique ID if it is not already allocated. 

```scala
val uniqueIdOfMyView = myView.uniqueId
```

One of the good use case of `uniqueId` is `SRelativeLayout`. Some of the methods in this layout context, such as `below`, `above`, `leftOf` and `rightOf`, takes another `View` object as an anchor:

```scala
new SRelativeLayout {
  val btn = SButton(R.string.hi)
  SButton("There").<<.below(btn)
}
```

Here we show the implementation of the `below` function:

```scala
def below(anchor: View)(implicit activity: Activity) = {
  addRule(RelativeLayout.BELOW, anchor.uniqueId)
  this
}
```

A new unique ID is assigned to the `anchor` if it is not assigned already, and passes it to `addRule` function. 

## Logging

Unlike other logging frameworks, Android Logging API requires a `String` tag for every log call. We eliminate this by introducing an implicit parameter. Define an implicit value type of `LoggerTag` as shown:

```scala
implicit val loggerTag = LoggerTag("MyAppTag")
```

or, extend trait `TagUtil` or `SContext` which defines the tag by default. Then you can simply log like this:

```scala
warn("Something happened!")
```

Other functions for every log level (`verbose()`, `debug()`, `info()`, `warn()`, `error()` and `wtf()`) are available.

```scala
info("hello " + world)
```

A `String` parameter passed with `info()` is a by-name parameter, so it is evaluated only if the logging is possible. Therefore, the example shown above is equivalent to:

```scala
val tag = "MyAppTag"
if(Log.isLoggable(tag, Log.INFO)) Log.i(tag, "hello " + world)
```


## Scala getters and setters

You can use any of the setters listed below:

* `obj.setText("Hello")` Java bean style
* `obj.text = "Hello"` Assignment style
* `obj text "Hello"` DSL style
* `obj.text("Hello")` Method calling style

Compared to Java style getters and setters, for example:

```scala
new TextView(context) {
  setText("Hello")
  setTextSize(15)
}
```

that of Scala style clearly reveals the nature of the operations as shown below:

```scala
new STextView {
  text = "Hello"
  textSize = 15
}
```

Or, you can also chain the setters:

```scala
new STextView text "Hello" textSize 15
```

which is a syntactic sugar for:

```scala
new STextView.text("Hello").textSize(15)
```

We recommend "assignment style" and "DSL style". Use assignment style when you emphasize that you are assigning something, or use DSL style when the code length of the assignee is short and needs to be chained.

Note: Using `.apply(String)` method on object `STextView`, you can further reduce the code above like this:

```scala
STextView("Hello") textSize 15
```

**Further readings:**

 * [Return value of setters](https://github.com/pocorall/scaloid/wiki/Basics#wiki-return-value-of-setters)
 * [Prefixed classes](https://github.com/pocorall/scaloid/wiki/Basics#wiki-prefixed-classes)
 * [Sweet-little sugar](https://github.com/pocorall/scaloid/wiki/Basics#wiki-sweet-little-sugar)


## Classes

### Class `AlertDialogBuilder`

A Scala-style builder for AlertDialog.
 
```scala
new AlertDialogBuilder(R.string.title, R.string.message) {
  neutralButton()
}.show()
```

This displays an alert dialog with given string resources. We provide an equivalent shortcut:

```scala
alert(R.string.title, R.string.message)
```

Also you can build a more complex dialog:

```scala
new AlertDialogBuilder("Exit the app", "Do you really want to exit?") {
  positiveButton("Exit", finishTheApplication())
  negativeButton(android.R.string.cancel)
}.show()
```

The code above is equivalent to:

```scala
new AlertDialog.Builder(context)
  .setTitle("Exit the app")
  .setMessage("Do you really want to exit?")
  .setPositiveButton("Exit", new DialogInterface.OnClickListener {
    def onClick(dialog: DialogInterface, which: Int) {
      finishTheApplication()
    }
  })
  .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener {
    def onClick(dialog: DialogInterface, which: Int) {
      dialog.cancel()
    }
  }).show()
```

When you call `show()` or `alert` from non-UI thread, you [don't have to mind about threading](https://github.com/pocorall/scaloid/wiki/Basics#wiki-asynchronous-task-processing).

### Class `SArrayAdapter`

Suppose you want to let the user selects a string from spinner, and larger font should be displayed in the dropdown list.
Then the plain-old Android code is consisted of a chunk of XML and its wiring:

```XML
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    style="?android:attr/spinnerDropDownItemStyle"
    android:id="@+id/spinner_textview"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
	android:textSize="25.dip" />
```
```scala
val adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, Array("One", "Two", "Three"))
adapter.setDropDownViewResource(R.layout.spinner_dropdown)
```

In Scaloid, a directly equivalent code is:

```scala
SArrayAdapter("One", "Two", "Three").dropDownStyle(_.textSize(25.dip))
```

If you want to let the text color in the spinner be blue, use the `style` method:

```scala
SArrayAdapter("Quick", "Brown", "Fox").style(_.textColor(Color.BLUE))
```

Can it be simpler?

### Class `LocalService`

[Android Developer Guide on service binding](http://developer.android.com/guide/components/bound-services.html) says that we have to write more than 60 lines of code to define and bind an in-process service. 
With Scaloid, you can concisely define and access local service as shown below:

```scala
class MyService extends LocalService {
  private val generator = new Random()

  def getRandomNumber() = generator.nextInt(100)
}

class Activity extends SActivity {
  val random = new LocalServiceConnection[MyService]
 
  def onButtonClick(v:View) {
    random( s => toast("number: " + s.getRandomNumber()))
  }
}
```

**Further reading:** Refer to [this blog post](http://blog.scaloid.org/2013/03/introducing-localservice.html) to see why this is awesome in compared with the existing method.

### Class `Preferences`

SharedPreference can be accessed in this way:

```scala
val executionCount = preferenceVar(0) // default value 0
val ec = executionCount() // read
executionCount() = ec + 1 // write
executionCount.remove() // remove
```

**Further reading:**
 - [Type-safe SharedPreference](http://blog.scaloid.org/2015/07/type-safe-sharedpreference.html)
 - [A simple example: Prompt user to rate your app](http://blog.scaloid.org/2013/03/prompt-user-to-rate-your-android-app.html)

## Extending View class
Often we need to define a custom view widget for a specific requirement.
To do this, we define a class that inherits `android.widget.View` class or its subclass (e.g. `TextView` and `Button`).
To enable Scaloid extensions for this custom widget, you can define a class as follows:

```scala
class MyView(implicit ctx: Context) extends View(ctx) with TraitView[MyView] {
  def basis = this
  
  // custom code for MyView here
}
```

## Let's make it together!

Scaloid is an Apache licensed project. 
If you have any idea to improve Scaloid, feel free to open issues or post patches.
If you want look into inside of Scaloid, this document would be helpful:

 * [Inside Scaloid](https://github.com/pocorall/scaloid/wiki/Inside-Scaloid)

 
### [List of projects using Scaloid](https://github.com/pocorall/scaloid/wiki/Appendix#wiki-list-of-projects-using-scaloid)

### We are hiring!
The company behind Scaloid, onsquare is hiring Scala developers.
We are building [a music app](https://play.google.com/store/apps/details?id=com.soundcorset.client.android) and other amazing products.
We extensively uses Scaloid in our product, and probably it is the best reference of Scaloid application.
For more information about our company, please refer to our website http://o-n2.com .
Please send us your CV via email if you are interested in working at onsqure.
We are located at Incheon, Korea.
pocorall@gmail.com
