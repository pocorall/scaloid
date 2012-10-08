# android-scala-common

Scala is cool. Writing Android application with Scala is also a cool idea. Because Android exposes Java API, we need some wrapper and utility library to leverage full power of Scala. android-scala-common is an initial attempt to provide this.

# Implicit conversions
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

# Import it to your project

For now, android-scala-common is a single-file project. Just copy ScalaUtils.scala and paset it to your project. Enjoy!

# Let's make it together!

This project is very early stage, and I will grow it constantly. If you have any idea to improve this library, feel free to open issues or post patches.

### License

This software is licensed under the Apache 2 license.

### Recommended resources

To set up a Maven project that build Android app written in Scala, please refer https://github.com/rohansingh/android-scala-test.

A delicate problem related to implicit conversion for mult-line block is discussed at  http://stackoverflow.com/questions/12774840/scala-passing-function-parameter-which-does-not-have-parameter.
