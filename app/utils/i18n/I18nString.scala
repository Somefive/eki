package utils.i18n

import play.api.libs.json.{JsObject, JsString, JsValue, Writes}

import scala.language.implicitConversions

object I18nString {
  def apply(zh: String = "", en: String = ""): I18nString = I18nString(Map(
    I18n.LANG_ZH -> zh,
    I18n.LANG_EN -> en
  ))
  implicit def apply(pair: (String, String)): I18nString = I18nString(pair._1, pair._2)
}

case class I18nString(map: Map[String, String]) extends I18n[String]("")(map) {
  def toJson: JsValue = {
    JsObject(map.map(p => p._1 -> JsString(p._2)))
  }
}
