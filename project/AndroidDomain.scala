
case class ScalaType(
  name: String,
  simpleName: String,
  params: Seq[ScalaType],
  isVar: Boolean
)

object ScalaType {
  def apply(name: String, params: Seq[ScalaType] = Nil, isVar: Boolean = false): ScalaType =
    ScalaType(name, name.split('.').last, params, isVar)
}

case class AndroidMethod(
  name: String,
  retType: ScalaType,
  argTypes: Seq[ScalaType],
  paramedTypes: Seq[ScalaType],
  isAbstract: Boolean = false,
  isOverride: Boolean = false
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
  setterArgTypes: Seq[ScalaType],
  callbackClassName: String,
  callbackMethods: Seq[AndroidCallbackMethod]
) {
  def isSafe: Boolean =
    (! setter.startsWith("set")) || callbackMethods.length == 1 || callbackMethods.forall(_.retType.name == "Unit")
}

case class ScalaConstructor(
  argTypes: Seq[ScalaType],
  implicitArgTypes: Seq[ScalaType],
  explicitArgTypes: Seq[ScalaType]
)

case class AndroidClass(
  name: String,
  pkg: String,
  tpe: ScalaType,
  parentType: Option[ScalaType],
  constructors: Seq[ScalaConstructor],
  properties: Seq[AndroidProperty],
  listeners: Seq[AndroidListener],
  isA: Set[String],
  isAbstract: Boolean,
  isFinal: Boolean
)

