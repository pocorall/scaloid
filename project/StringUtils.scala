


object StringUtils {
  def decapitalize(s: String) = if (s.isEmpty) s else s(0).toLower + s.substring(1)
  def simpleName(s: String) = s.split('.').last
  def toJavaConst(s: String) = (s.head +: "[A-Z]".r.replaceAllIn(s.tail, m => "_"+m.group(0))).toUpperCase
  def managerToService(s: String) = {
    val jc = toJavaConst(s.replace("DropBox", "Dropbox"))
    (if (jc.endsWith("MANAGER")) jc.split('_').init.mkString("_")
    else jc) + "_SERVICE"
  }

  def dotToSlash(s: String) = s.replace(".", "/")

  private val reservedKeywordsNotInJava =
    Set("def", "extends", "implicit", "import", "match", "lazy", "object", "package",
      "requires", "sealed", "trait", "type", "val", "var", "with", "yield")
  def safeIdent(s: String) = if (s.matches("^[0-9].*") || reservedKeywordsNotInJava(s)) "`"+s+"`" else s

  def span(s: String, i: Int) = s.padTo(i, " ").mkString

  val deprecatedDecl = """@deprecated("", "") """
}

