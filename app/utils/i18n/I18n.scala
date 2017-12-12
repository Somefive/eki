package utils.i18n

object I18n {
  val LANG_ZH = "zh"
  val LANG_EN = "en"
}

class I18n[T](default: T)(map: Map[String, T]) {
  def get(lang: String): T = map.getOrElse(lang, default)
  def get(lang: String, overrideDefault: T): T = map.getOrElse(lang, overrideDefault)
  def toApiResp: Map[String, Any] = map
  lazy val zh: T = get(I18n.LANG_ZH)
  lazy val en: T = get(I18n.LANG_EN)
}
