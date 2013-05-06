
case class ScalaType(
  name: String,
  params: Seq[ScalaType] = Nil,
  isVar: Boolean = false
)

case class AndroidMethod(
  name: String,
  retType: ScalaType,
  argTypes: Seq[ScalaType],
  paramedTypes: Seq[ScalaType],
  isAbstract: Boolean = false
)

case class AndroidCallbackMethod(
  name: String,
  retType: ScalaType,
  argTypes: Seq[ScalaType],
  hasBody: Boolean = true
)

case class AndroidProperty(
  name: String,
  tpe: ScalaType,
  getter: Option[AndroidMethod],
  setters: Seq[AndroidMethod],
  switch: Option[String],
  nameClashes: Boolean
)

case class AndroidListener(
  name: String,
  retType: ScalaType,
  argTypes: Seq[ScalaType],
  hasParams: Boolean,
  setter: String,
  callbackClassName: String,
  callbackMethods: Seq[AndroidCallbackMethod]
) {
  def isSafe: Boolean =
    (! setter.startsWith("set")) || callbackMethods.length == 1 || callbackMethods.forall(_.retType.name == "Unit")
}

case class AndroidClass(
  name: String,
  pkg: String,
  tpe: ScalaType,
  parentType: Option[ScalaType],
  properties: Seq[AndroidProperty],
  listeners: Seq[AndroidListener],
  isA: Set[String],
  isAbstract: Boolean
)

