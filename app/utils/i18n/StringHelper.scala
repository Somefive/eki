package utils.i18n

object StringHelper {
  implicit class _String(raw: String) {
    def splitAndNonEmpty(regex: String): List[String] = raw.split(regex).filter(_.nonEmpty).map(_.trim).toList
    def splitAndNonEmpty(separator: Char): List[String] = raw.split(separator).filter(_.nonEmpty).map(_.trim).toList
    def notNullOrElse(default: String = ""): String = if (raw != null) raw else default
  }
}
