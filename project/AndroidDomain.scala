
case class ScalaType(
  name: String,
  simpleName: String,
  params: List[ScalaType],
  bounds: List[ScalaType],
  isVar: Boolean,
  javaName: String
)

object ScalaType {
  def apply(name: String, params: List[ScalaType] = Nil, bounds: List[ScalaType] = Nil, isVar: Boolean = false): ScalaType =
    ScalaType(name, name.split('.').last, params, bounds, isVar, name /* reuse scala name by default */)
}

case class Argument(
  name: String,
  tpe: ScalaType
)

case class AndroidMethod(
  name: String,
  retType: ScalaType,
  argTypes: List[ScalaType],
  paramedTypes: List[ScalaType],
  isAbstract: Boolean,
  isOverride: Boolean,
  isDeprecated: Boolean
)

case class AndroidCallbackMethod(
  name: String,
  retType: ScalaType,
  argTypes: List[ScalaType],
  hasBody: Boolean,
  isDeprecated: Boolean
)

case class AndroidProperty(
  name: String,
  tpe: ScalaType,
  getter: Option[AndroidMethod],
  setters: List[AndroidMethod],
  switch: Option[String],
  nameClashes: Boolean
)

case class AndroidListener(
  name: String,
  retType: ScalaType,
  argTypes: List[ScalaType],
  hasParams: Boolean,
  setter: String,
  setterArgTypes: List[ScalaType],
  callbackClassName: String,
  callbackMethods: List[AndroidCallbackMethod],
  isDeprecated: Boolean
) {
  def isSafe: Boolean =
    (! setter.startsWith("set")) || callbackMethods.length == 1 || callbackMethods.forall(_.retType.name == "Unit")
}

case class AndroidIntentMethod (
  name: String,
  retType: ScalaType,
  argTypes: List[ScalaType],
  zeroArgs: Boolean,
  isDeprecated: Boolean
)

case class ScalaConstructor(
  args: List[Argument],
  implicitArgs: List[Argument],
  explicitArgs: List[Argument],
  paramedTypes: List[ScalaType],
  isVarArgs: Boolean,
  isDeprecated: Boolean
)

case class AndroidClass(
  name: String,
  pkg: String,
  tpe: ScalaType,
  parentType: Option[ScalaType],
  constructors: List[ScalaConstructor],
  properties: List[AndroidProperty],
  listeners: List[AndroidListener],
  intentMethods: List[AndroidIntentMethod],
  isA: Set[String],
  isAbstract: Boolean,
  isFinal: Boolean,
  hasBlankConstructor: Boolean,
  isDeprecated: Boolean
)

