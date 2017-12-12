package models

import org.apache.lucene.document._
import utils.i18n.I18nString
import utils.i18n.StringHelper._
import play.api.libs.json._

object Publication {
  def zip(zh: List[String], en: List[String]): List[I18nString] = zh.zipAll(en, "", "").map(I18nString(_))
  def apply(map: Map[String, String]): Publication = {

    def get(label: String): String = map.getOrElse(label, "").stripSuffix(";")
    def getInt(label: String): Int = map.getOrElse(label, "0").toInt
    def extract(zh: String, en: String): I18nString = get(zh) -> get(en)

    val authors_zh = get("作者").splitAndNonEmpty(';')
    val raw_authors_en = get("英文作者").splitAndNonEmpty("[()]")
    val authors_en = raw_authors_en.headOption.map(_.splitAndNonEmpty(',')).getOrElse(List())
    val authors = zip(authors_zh, authors_en)
    val raw_firstPerson = get("第一责任人").splitAndNonEmpty(';').headOption.getOrElse("")
    val keywords_zh = get("关键词").splitAndNonEmpty(";;")
    val keywords_en = get("英文关键词").splitAndNonEmpty(';')
    Publication(
      filename = get("文件名"),
      title = extract("题名", "英文篇名"),
      authors = authors,
      firstPerson = authors.find(_.zh == raw_firstPerson).getOrElse(I18nString()),
      org = get("单位") -> (if (raw_authors_en.length > 1) raw_authors_en(1) else ""),
      src = extract("来源", "英文刊名"),
      publisher = get("出版单位") -> "",
      keywords = zip(keywords_zh, keywords_en),
      `abstract` = extract("摘要", "英文摘要"),
      year = getInt("年"),
      volume = get("期"),
      issueCode = get("专辑代码"),
      topicCode = get("专题代码"),
      subTopicCode = get("专题子栏目代码"),
      topics = get("专题名称").splitAndNonEmpty(';'),
      categoryNumber = get("分类号"),
      category = get("分类名称"),
      lang = get("语种"),
      refYears = get("被引年").splitAndNonEmpty(';').map(_.toInt),
      refs = get("参考文献").splitAndNonEmpty(';'),
      sourceRef = get("引证文献").splitAndNonEmpty(';'),
      commonRef = get("共引文献").splitAndNonEmpty(';'),
      subRef = get("二级引证文献").splitAndNonEmpty(';'),
      table = get("表名"),
      publishDate = get("出版日期"),
      refCount = getInt("引证文献数量"),
      subRefCount = getInt("二级参考文献数量"),
      subSourceCount = getInt("二级引证文献数量"),
      commonRefCount = getInt("共引文献数量"),
      commonSourceCount = getInt("同被引文献数量"),
      ISSN = get("ISSN"),
      CN = get("CN"),
      fund = get("基金").splitAndNonEmpty(";;")
    )
  }

  def apply(doc: Document): Publication = {
    def get(fieldName: String): String = doc.get(fieldName).notNullOrElse()
    def getInt(fieldName: String): Int = doc.get(fieldName).notNullOrElse("0").toInt
    def getI18n(fieldName: String): I18nString = get(fieldName + "_zh") -> get(fieldName + "_en")
    def getI18nList(fieldName: String): List[I18nString] = zip(doc.getValues(fieldName + "_zh").toList, doc.getValues(fieldName + "_en").toList)
    def getIntList(fieldName: String): List[Int] = doc.getValues(fieldName).map(_.notNullOrElse("0").toInt).toList
    def getList(fieldName: String): List[String] = doc.getValues(fieldName).toList
    Publication(
      get("filename"),
      getI18n("title"),
      getI18nList("authors"),
      getI18n("firstPerson"),
      getI18n("org"),
      getI18n("src"),
      getI18n("publisher"),
      getI18nList("keywords"),
      getI18n("abstract"),
      getInt("year"),
      get("volume"),
      get("issueCode"),
      get("topicCode"),
      get("subTopicCode"),
      getList("topics"),
      get("categoryNumber"),
      get("category"),
      get("lang"),
      getIntList("refYears"),
      getList("refs"),
      getList("sourceRef"),
      getList("commonRef"),
      getList("subRef"),
      get("table"),
      get("publishDate"),
      getInt("refCount"),
      getInt("subRefCount"),
      getInt("subSourceCount"),
      getInt("commonRefCount"),
      getInt("commonSourceCount"),
      get("ISSN"),
      get("CN"),
      getList("fund")
    )
  }
}

case class Publication(
  filename: String,
  title: I18nString,
  authors: List[I18nString],
  firstPerson: I18nString,
  org: I18nString,
  src: I18nString,
  publisher: I18nString,
  keywords: List[I18nString],
  `abstract`: I18nString,
  year: Int,
  volume: String,
  issueCode: String,
  topicCode: String,
  subTopicCode: String,
  topics: List[String],
  categoryNumber: String,
  category: String,
  lang: String,
  refYears: List[Int],
  refs: List[String],
  sourceRef: List[String],
  commonRef: List[String],
  subRef: List[String],
  table: String,
  publishDate: String,
  refCount: Int,
  subRefCount: Int,
  subSourceCount: Int,
  commonRefCount: Int,
  commonSourceCount: Int,
  ISSN: String,
  CN: String,
  fund: List[String]
) {

  lazy val toDocument: Document = {
    val doc = new Document()
    addField(doc, "filename")
    addField(doc, "title", index = true)
    addField(doc, "authors", index = true)
    addField(doc, "firstPerson", index = true)
    addField(doc, "org", index = true)
    addField(doc, "src", index = true)
    addField(doc, "publisher", index = true)
    addField(doc, "keywords", index = true)
    addField(doc, "abstract", index = true)
    addField(doc, "year")
    addField(doc, "volume")
    addField(doc, "issueCode")
    addField(doc, "topicCode")
    addField(doc, "subTopicCode")
    addField(doc, "topics", index = true)
    addField(doc, "categoryNumber")
    addField(doc, "category", index = true)
    addField(doc, "lang")
    addField(doc, "refYears")
    addField(doc, "refs")
    addField(doc, "sourceRef")
    addField(doc, "commonRef")
    addField(doc, "subRef")
    addField(doc, "table")
    addField(doc, "publishDate")
    addField(doc, "refCount")
    addField(doc, "subRefCount")
    addField(doc, "subSourceCount")
    addField(doc, "commonRefCount")
    addField(doc, "commonSourceCount")
    addField(doc, "ISSN")
    addField(doc, "CN")
    addField(doc, "fund", index = true)
    doc
  }

  private def addField(doc: Document, fieldName: String, index: Boolean = false): Unit = {
    val field = this.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    def add(value: Any): Unit = {
      value match {
        case value: I18nString =>
          if (index) {
            doc.add(new TextField(fieldName+"_zh", value.zh, Field.Store.YES))
            doc.add(new TextField(fieldName+"_en", value.en, Field.Store.YES))
          } else {
            doc.add(new StringField(fieldName+"_zh", value.zh, Field.Store.YES))
            doc.add(new StringField(fieldName+"_en", value.en, Field.Store.YES))
          }
        case value: String =>
          if (index)  doc.add(new StringField(fieldName, value, Field.Store.YES))
          else        doc.add(new TextField(fieldName, value, Field.Store.YES))
        case value: Integer =>
          doc.add(new IntPoint(fieldName, value))
          doc.add(new StoredField(fieldName, value))
        case value: List[Any] =>
          value.foreach(add)
        case _ =>
      }
    }
    add(field.get(this))
  }

  def toApiResp: Map[String, Any] = {
    val fields = this.getClass.getDeclaredFields
    fields.foreach(_.setAccessible(true))
    def to(value: Any): Any = {
      value match {
        case value: I18nString => value.toApiResp
        case value: String => value
        case value: Integer => value
        case value: List[Any] => value.map(to)
        case _ => null
      }
    }
    fields.map(field => field.getName -> to(field.get(this))).toMap
  }

  def toJson: JsValue = {
    val fields = this.getClass.getDeclaredFields
    fields.foreach(_.setAccessible(true))
    def to(value: Any): JsValue = {
      value match {
        case value: I18nString => value.toJson
        case value: String => JsString(value)
        case value: Integer => JsNumber(BigDecimal(value))
        case value: List[Any] => JsArray(value.slice(0, 20).map(to))
        case _ => JsNull
      }
    }
    JsObject(fields.map(field => field.getName -> to(field.get(this))).toMap)
  }
}
