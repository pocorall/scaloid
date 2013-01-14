<p align="center"><img src="http://o-n2.com/scaloid_logo.png"></p>

# Less painful Android development with Scala

Scala is cool. [Writing Android applications with Scala](#faqs-about-scala-on-android) is also a cool idea. Because Android exposes Java APIs, we need some utility library to leverage full power of Scala. Scaloid is a library aimed to simplify your Android code.

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
   Scaloid provides a concise and type-safe way of writing Android application.
 * **Simple to use**<br/>
   Check our [quick start guide](#quick-start)
 * **Compatible with your legacy code**<br/>
   You can use both Scaloid and plain-old Java Android API. You can gradually improve your legacy code.
 * **Maintained actively**<br/>
   Scaloid is a [dogfooding](http://en.wikipedia.org/wiki/Eating_your_own_dog_food) software. This is originally created to be used for [my own](https://play.google.com/store/apps/details?id=com.soundcorset.client.android) [Android apps](https://play.google.com/store/apps/details?id=com.tocplus.client.android).

### Demos

If you want to see how Scaloid can be used in action, check a [Scala port of apidemos app](https://github.com/pocorall/scaloid-apidemos).

##### Need help using Scala language on your Android project?

There is an out-of-the-box solution. Just [fork this project](https://github.com/rohansingh/android-scala-test) and start your Scala-Android app.

 * [FAQs about Scala on Android](#faqs-about-scala-on-android)

## Features

 * [UI Layout without XML](#ui-layout-without-xml)
 * [Implicit conversions](#implicit-conversions)
 * [Shorter representation without context object](#context-as-an-implicit-parameter)
 * [Shorter listeners](#enriched-implicit-classes)
 * [Asynchronous processing](#trait-runonuithread)
 * [Smarter logging](#logging)
 * [Improved getters/setters](#scala-getters-and-setters)
 * [Concise dialog builder](#class-alertdialogbuilder)

...and many other things!

## UI Layout without XML

Android SDK leverages XML to build UI layouts. However, XML considered still a bit verbose, and lacks programmability. Scaloid composes UI layout in Scala DSL style, therefore achieve both clarity and programmability. For example, suppose a legacy XML layout as shown below:

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
            android:layout_width="match_parent"
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
  STextView("Sign in").textSize(24.5 sp).<<.marginBottom(25 dip).>>
  STextView("ID")
  SEditText()
  STextView("Password")
  SEditText() inputType TEXT_PASSWORD
  SButton("Sign in")
    += (new SLinearLayout {
      SButton("Help")
      SButton("Sign up")
    })
}.padding(20 dip)
```    

The layout description shown above is highly programmable. You can easily wire your logic into the layout:

```scala
new SVerticalLayout {
  STextView("Sign in").textSize(24.5 sp).<<.marginBottom(25 dip).>>
  STextView("ID")
  val userId = SEditText()
  STextView("Password")
  val pass = SEditText() inputType TEXT_PASSWORD
  SButton("Sign in", signin(userId.text, pass.text))
    += (new SLinearLayout {
      SButton("Help", openUri("http://help.url"))
      SButton("Sign up", openUri("http://signup.uri"))
    })
}.padding(20 dip)
```

Because a Scaloid layout description is plain Scala code, it is type-safe. Please refer to [layout context](#layout-context) for more details.

### Automatic layout converter

This converter turns an Android XML layout into a Scaloid layout:

http://layout.scaloid.org


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
openUri("http://google.com")
```    

, or wherever you want.

##### Resource IDs

```scala
Int => CharSequence
Int => Drawable
```  

Android API provides two versions of methods for string resources; One for `CharSequence`, the other for `Int` as a resource ID. If you write a function that handles Android resource, you also have to expose methods for every combination of two versions of resources:

```scala
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
```    

This is not a smart way. Write just one method that defines the logic:

```scala
def alert(title:CharSequence, text:CharSequence) = ...
```    

Then Scaloid implicit conversions will take care about these resource type conversions.

##### Unit conversion

Units `dip` and `sp` can be converted into the pixel unit.

```scala
val inPixel:Int = (32 dip)
val inPixel2:Int = (22 sp)
```  


##### Runnable

```scala
( => Any) => Runnable
```  

`Runnable` also covered with [rich](#rich-classes) and [prefixed classes](#prefixed-classes).

There are more implicit conversions available. Check the source code as needed.

## Context as an implicit parameter
Many methods in the Android API require an instance of a class `Context`. Providing this for every method call results in clumsy code. We employ an implicit parameter to eliminate this. Just declare an implicit value that represents current context:

```scala
implicit val context = ...
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

##### Toast

```scala
Toast.makeText(context, "hi, there!", Toast.LENGTH_SHORT).show()
```

is reduced to:

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

##### DefaultSharedPreferences

```scala
PreferenceManager.getDefaultSharedPreferences(context)
```

is reduced to:

```scala
defaultSharedPreferences
```

##### Play ringtones

Just play the default notification ringtone:

```scala
play()
```

specify ringtone resources as a `String`:

```scala
play("content://media/internal/audio/media/50")
```

or specify a resource `Uri`:

```scala
play(alarmSound)
```

##### Open URIs

This opens a web browser (or another view assigned to the http protocol).

```scala
openUri("http://google.com")
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
    openUri("http://google.com")
  }
})
```  

Scaloid provides a shortcut that dramatically reduces the length of the code:

```scala
find[Button](R.id.search).onClick(openUri("http://google.com"))
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

In the anonymous constructor of 'SLinearLayout', Scaloid provides an implicit function called "layout context". This affects a return type of the method `<<` defined in the class `SButton`. If we use `SFrameLayout` as a layout context, the method `<<` returns `FrameLayout.LayoutParams`, so the code below results a ___syntax error___.

```scala
val layout = new SFrameLayout {
  SButton("Click").<<.Weight(1.0f).>>   // Syntax error on Weight()
}
```    

Compared with XML layout description, Scaloid layout is simple and type-safe.

The method `<<` is overloaded with parameters `<<(width:Int, height:Int)` which assignes the size of the view component. For example:

```scala
SButton("Click").<<(40 dip, WRAP_CONTENT)
```    

#### Operator `new` and method `apply`

Usually, `View` components are referenced multiple times in an `Activity`. For example:

```scala
var button: SButton = null
override def onCreate(savedInstanceState: Bundle) {
  // ...
  new SLinearLayout {
    button = new SButton() text "Click"
      +=(button)
  }
  // ...
}
// ... uses the button somewhere in other methods (e.g. changing text or adding listeners)
```  

[Prefixed classes](#prefixed-classes) in Scaloid (e.g. `SButton`) have a companion object that implements `apply` methods that create a new component. These methods also append the component to the layout context that enclose the component. Therefore, the code block from the above example:

```scala
button = new SButton() text "Click"
  +=(button)
```    

is equivalent to:

```scala
button = SButton("Click")
```    

Because the `apply` methods access to the layout context, it cannot be called outside of the layout context. In this case, use the `new` operator instead.

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
  +=(new SLinearLayout {
    SButton("Click").<<.Weight(1.0f).>>   // in context of SLinearLayout
  })
}
```

#### Methods `fill` and `warp`

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


#### Naming conventions

Scaloid follows the naming conventions of XML attributes in the Android API with some improvements.

For XML attributes, layout related properties are prefixed with `layout_` and as you might have guessed, Scaloid does not need it. For boolean attributes, the default is `false`. However, Scaloid flags it as `true` when the attribute is declared explicitly without any parameter. For example:

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
STextView("hello").<<.margin(5 dip, 10 dip, 5 dip, 10 dip)
```    

or

```scala
STextView("hello").<<.margin(10 sp)  // assigns the same value for all directions
```


#### Tip: Styles for programmers

Android API introduced [styles](http://developer.android.com/guide/topics/ui/themes.html) to reuse common properties on XML layout. Meanwhile, Scaloid layout is an ordinary Scala code. Therefore we can freely define some functions that works as styles. Suppose the following code that repeats some properties:

```scala
SButton("first").textSize(20 dip).<<.margin(5 dip).>>
SButton("prev").textSize(20 dip).<<.margin(5 dip).>>
SButton("next").textSize(20 dip).<<.margin(5 dip).>>
SButton("last").textSize(20 dip).<<.margin(5 dip).>>
```  

Then we can define a function that applies these properties:

```scala
def myStyle = (_: SButton).textSize(20 dip).<<.margin(5 dip).>>
myStyle(SButton("first"))
myStyle(SButton("prev"))
myStyle(SButton("next"))
myStyle(SButton("last"))
```  

Still not satisfying? Here we have a shorter one:

```scala
def myStyle = (_: SButton).textSize(20 dip).<<.margin(5 dip).>>
List("first", "prev", "next", "last").foreach(title => myStyle(SButton(title)))
```

## Traits


### Trait `RunOnUiThread`

Android API provides `runOnUiThread()` only for class `Activity`. Trait `RunOnUiThread` provides Scala version of `runOnUiThread()` for anywhere other than `Activity` (e.g. `Service`).

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

extend trait `RunOnUiThread` and use it like this:

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

Using `runOnUiThread` and importing `scala.concurrent.ops._`, the asynchronous job shown above can be rewritten like this:

```scala
spawn {
  val result = doAJobTakeSomeTime(params)
  runOnUiThread(alert("Done!", result))
}
```  

It is a great win as it exposes your idea clearly.

Just like we throw away `AsyncTask`, we can also elliminate all other Java helpers for asynchronous job, such as `AsyncQueryHandler` and `AsyncTaskLoader`. Compare with the [original Java code](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/view/ExpandableList2.java?av=h)
and a [Scala port](https://github.com/pocorall/scaloid-apidemos/blob/master/src/main/java/com/example/android/apis/view/ExpandableList2.scala) of ApiDemos example app.

Using `spawn` is just an example of asynchronous task processing in Scaloid. You can freely use any modern task management utility such as [futures and promises](http://docs.scala-lang.org/sips/pending/futures-promises.html).


### Trait `UnregisterReceiverService`

When you registere `BroadcastReceiver` with `Context.registerReceiver()` you have to unregister it to prevent memory leak. Trait `UnregisterReceiverService` handles these chores for you. All you need to do is extend your Service.

```scala
class MyService extends UnregisterReceiverService {
  def func() {
    // ...
    registerReceiver(receiver, intentFilter)
    // Done! automatically unregistered at UnregisterReceiverService.onDestroy()
  }
}
```

### Trait `SContext`

Trait `SContext` includes several shortcuts for frequently used android idioms, and inherits `TagUtil` and `RunOnUiThread`.

##### Starting and stopping service

```scala
startService(new Intent(context, classOf[MyService]))
stopService(new Intent(context, classOf[MyService]))
```    

is reduced to:

```scala
startService[MyService]
stopService[MyService]
```    


##### Starting activity

```scala
startActivity(new Intent(context, classOf[MyActivity]))
```    

is reduced to:

```scala
startActivity[MyActivity]
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

Although we provide this shorthand, Scaloid recommends [programmatically laying out UI, not with XML](#ui-layout-without-xml).

## Logging

Unlike other logging frameworks, Android Logging API requires a `String` tag for every log call. We elliminate this by introducing an implicit parameter. Define an implicit value type of `LoggerTag` as shown:

```scala
implicit val tag = LoggerTag("MyAppTag")
```    

or, extend trait `TagUtil` or `SContext` which defines the tag by default. Then you can simply log like this:

```scala
warn("Something happened!")
```  

Other functions for every log level (`verbose()`, `debug()`, `info()`, `warn()`, `error()` and `wtf()`) are available.

```scala
info("hello " + world)
```

A `String` parameter passed with `info()` is a lazy argument, so it is evaluated only if the logging is possible. Therefore the example shown above is equivalent to:

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


### Return value of setters

Unlike most setters in the Android API, our setters return the object itself. This feature can be used as a syntactic sugar when setters need to be chained or a function returning some object. For example, a snippet of [Java code](http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.1.1_r1/com/example/android/apis/view/ExpandableList1.java?av=h) from ApiDemos that is shown below:

```java
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
  TextView textView = getGenericView();
  textView.setText(getGroup(groupPosition).toString());
  return textView;
}
```    

is reduced to:

```scala
def getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View, parent: ViewGroup): View =
  getGenericView.text = getGroup(groupPosition).toString
```      

<sub>**Design considerations on returning values:** In C or Java, the assignment operator `=` returns a right hand side object. However, chaining assignment operator is very rarely used in these languages. Assigning the same value to multiple variables might means that your code is badly designed (except some context such as involving intensive mathematical computations). However, in Scala DSLs, setters return a left hand side object, and chaining setters are more frequent. For example:</sub>

getGenericView text "hello" maxHeight 8

### Prefixed classes

If you want to use scala style getters/setters, implicit conversions do the magic on native Android objects:

```scala
val v: TextView = ...
v.text = "Hello"    // Valid code. Implicit conversion handles this.
```

However, if you use it in constructors, the compiler will not find the correct implicit conversion:

```scala
def getInstance = new TextView(context) {
  text = "Hello"    // Compilation Error.
}
```  

Therefore, we extended Android classes with the same name prefixed with the 'S' character:

```scala
def getInstance = new STextView {
  text = "Hello"    // OK.
}
```

These classes explicitly provide the extra methods that was provided implicitly.

Aditionally, prefixed classes support [implicit context value](#context-as-an-implicit-parameter) and additional syntactic sugar. For example, many classes have `.apply(...)` methods for creating a new instance:

```scala
STextView("Hello")
SButton("title", onClickBehavior())
SIntent[MyActivity]
```

<sub>**Design considerations on making prefixed classes:** In modern programming language, using packages (or namespaces) are preferred than prefixing. However, when we use both classes from Android API and Scaloid, using a package name is more verbose than prefixing the class name itself (compare with `common.Button` and `SButton`) and can be confused when you use both classes at the same code. We choose pragmatism rather than discipline.</sub>

### Sweet-little sugar

If the setter ends with `...Enabled`, Scaloid adds functions named `enable...` and `disable...`. For example:

```scala
new SLinearLayout().disableVerticalScrollBar
```

is equivalent to:

```scala
new SLinearLayout().verticalScrollBarEnabled(false)
```

Because setting the property `orientation = VERTICAL` for `SLinearLayout` is frequently used, we provide a shorthand:

```scala
new SVerticalLayout()
```

that is equivalent to:

```scala
new SLinearLayout().orientation(LinearLayout.VERTICAL)
```


## Classes

### Class AlertDialogBuilder

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
  negativeButton("Cancel")
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
  .setNegativeButton("Cancel", new DialogInterface.OnClickListener {
    def onClick(dialog: DialogInterface, which: Int) {
      dialog.cancel()
    }
  }).show()
```

## Static fields on protected interfaces

Android API has some protected interfaces which has static fields, and inherited it in public classes. For example `android.provider.ContactsContract.Contacts` inherits a protected interface `android.provider.ContactsContract.ContactsColumns`, which defines a static field `ContactsColumns.DISPLAY_NAME`. In Java code, you can access to it with `Contacts.DISPLAY_NAME`. However, Scala does not support accessing in this way (please refer [this](https://issues.scala-lang.org/browse/SI-1806) and [this](http://www.scala-lang.org/faq/4)). It is bad news for an Android-Scala programmer. So we provide a workaround implementation for this problem. Just copy-and-paste `Workaround.java` and declare `import org.scaloid.Workarounds._`. Then you can use the interfaces publicly which is originally defined as protected.

## Quick start

 1. [Import Scaloid to your project](#import-it-to-your-project)
 1. Declare `import org.scaloid.common._` in your code.
 1. Modify the signature of your classes
  * If your class inherits `Activity`, change it to `SActivity`
  * If your class (indirectly) inherits `Context`, add `trait SContext with LoggerTag`
  * Otherwise, setting an implicit value is required <br/>
    `implicit val context: Context = ...`

Then, you are ready to use Scaloid.

If you want to see how Scaloid can be used in action, check a [Scala port of apidemos app](https://github.com/pocorall/scaloid-apidemos).

## Import it to your project

### As a maven artifact
Scaloid is released to the central maven repository.

For maven:

```xml
<dependency>
    <groupId>org.scaloid</groupId>
    <artifactId>scaloid</artifactId>
    <version>0.6_8_2.9</version>
</dependency>
```

For sbt:

```scala
libraryDependencies += "org.scaloid" % "scaloid" % "0.6_8_2.9"
```

##### Version number
Version number of Scaloid is consisted of three parts, separated by `_` characters. The first part is the version of Scaloid, the second is the level of Android API, and the last one is the version of Scala.

Please note that Android API provides backward compatibility, while Scala does not provide it in a binary level. For now, the Scaloid artifact is compiled with Scala 2.9. If you use Scala 2.10 or higher, import the souce code as shown in the following subsection.


### As the source code
Scaloid is a single-file project. Just copy `common.scala` and paste it to your project.

 * Currently, Scaloid can be built with Android API level 8 or higher and Scala version 2.9.1 or higher.



## Let's make it together!

This project is in its early stages, and I will grow it constantly. If you have any idea to improve Scaloid, feel free to open issues or post patches.

### License

This software is licensed under the [Apache 2 license](http://www.apache.org/licenses/LICENSE-2.0.html).


## Roadmap

* **Cover full Android API versions** <br/>
  Currently, only API level 8 is supported. Scaloid may not be compiled with below that API level, and new features introduced above that level are not covered.
  Some of the features to be covered are:
  1. Fragment
  1. New system services
  1. Action bar

* **Build an example Android app** <br/>
  Finish a [Scala port of apidemos app](https://github.com/pocorall/scaloid-apidemos) and try another.
* **Build a dedicated website**
* **Write a complete API document**
* **Write the Beginner's guide**
* **Build an example of laying out multiple UI**
* **Build an example of cascaded layout style** <br/>
  Imagine a CSS for Android
* **Cover full listener shortcuts** <br/>
* **Cover OpenGL ES and renderscript**
* **Automatically unregister SensorEventListener onStop()**
* **Support Google services** <br/>
  Including Google Cloud Messaging (GCM)
* **Write a converter that turns an XML layout into a Scaloid code** <br/>
  [A simple web application](http://layout.scaloid.org) is demonstrated. Providing this functionality as an Eclipse or Intellij plugin would also be great.
* **WISIWIG layout builder**
* **iOS?**


## Appendix

### Why Scala rather than Xtend?

Xtend natively supports 1) converting Java bean style getter/setters into the assignment style 2) automatically shorten the clutters when calling one-method callback interface by converting it into closure-like style. Because these are language features, Xtend users can enjoy these features without any wrapper library. We hope that Scala also adopt these benefits soon.

However, We think that Scala is a better alternative for Android platform, because Scala is mature than Xtend and has these advanced features:

* **Implicit conversion** <br/>
  Check [implicit conversions](#implicit-conversions) section to see how Scaloid leverage it.

* **Implicit parameter** <br/>
  Hiding some common values (e.g. [context object](#context-as-an-implicit-parameter), [tag for logging](#logging), and [layout context](#layout-context)) from the parameter list is possible by the Scala's implicit parameters.

* **Advanced type system** <br/>
  Scaloid [layout parameter](#layout-context) is simple, intuitive, and type-safe.

* **Traits** <br/>
  Many useful features such as [automatic unregistering receivers](#trait-unregisterreceiverservice) are implemented as traits which permits multiple inheritance.


### Why Scala rather than JRuby?

* **Type-safety**

* **Runtime performance** (and your precious battery)<br/>
  See a [benchmark](http://shootout.alioth.debian.org/)

### FAQs about Scala on Android

Because programming in Scala on Android is not a widely known practice yet, many people asks me basic questions about it. Here are some frequently asked questions:

##### How big is the compiled apk?
For Scala + Android projects, using [proguard](http://proguard.sourceforge.net/) is mandatory. After the library is reduced by proguard, overhead caused by the Scala standard library is about several hundred kilobytes, although it depends on how much you used the library in your code.

##### How much slow the application?
According to a [benchmark](http://shootout.alioth.debian.org/), runtime performance of Scala is a little worse than that of Java. However, because most of the code using Scaloid is wiring UI and core logic, these performance difference is nearly not noticeable. Still, the display will consume most of the battery life, not Scala.

##### How much slow the compilation?
Compiling Scala source code and applying proguard takes some time. However, if you have a machine with a multi-core CPU and SSD, it would be a matter of few seconds.

##### Is it hard to setup a Scala + Android project?
It's not hard. There is an [out-of-the-box maven project template](https://github.com/rohansingh/android-scala-test).

(I did not try sbt on Android; Let me know if there are good sample projects on sbt.)
