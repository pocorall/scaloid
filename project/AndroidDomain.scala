case class AndroidMethod(
  name: String,
  retType: String,
  paramTypes: Seq[String]
)

case class AndroidCallbackMethod(
  name: String,
  retType: String,
  paramTypes: Seq[String],
  hasBody: Boolean = true
)

case class AndroidProperty(
  name: String,
  tpe: String,
  getter: Option[AndroidMethod],
  setters: Seq[AndroidMethod],
  switch: Option[String],
  nameClashes: Boolean
)

case class AndroidListener(
  name: String,
  retType: String,
  paramTypes: Seq[String],
  hasParams: Boolean,
  setter: String,
  callbackClassName: String,
  callbackMethods: Seq[AndroidCallbackMethod]
) {
  def isSafe: Boolean =
    (! setter.startsWith("set")) || callbackMethods.length == 1 || callbackMethods.forall(_.retType == "Unit")
}

case class AndroidClass(
  fullName: String,
  simpleName: String,
  `package`: String,
  parent: Option[String],
  isA: Set[String],
  properties: Seq[AndroidProperty],
  listeners: Seq[AndroidListener]
)

