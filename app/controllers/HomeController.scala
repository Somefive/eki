package controllers

import javax.inject.Inject

import engines.LuceneEngine
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.functional.syntax._


/**
  * A very small controller that renders a home page.
  */
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  case class QueryNode(field: String, value: String)
  implicit lazy val QueryNodeReads: Reads[QueryNode] = (
    (JsPath \ "field").read[String] and
    (JsPath \ "value").read[String]
  )(QueryNode)

  def simpleSearch(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[QueryNode].asOpt match {
      case Some(q) => Ok(JsArray(LuceneEngine.searchByField(q.field, q.value).slice(0, 20).map(_.toJson)))
      case None => BadRequest(JsObject(Seq("error" -> JsString("Invalid Format"))))
    }
  }

  case class GeneralQuery(qs: String, skip: Int, limit: Int, analyzer: String, strict: Boolean)
  implicit lazy val GeneralQueryReads: Reads[GeneralQuery] = (
    (JsPath \ "qs").read[String] and
    (JsPath \ "skip").read[Int] and
    (JsPath \ "limit").read[Int] and
    (JsPath \ "analyzer").read[String] and
    (JsPath \ "strict").read[Boolean]
  )(GeneralQuery)
  def generalSearch(): Action[JsValue] = Action(parse.json) { implicit request =>
    request.body.validate[GeneralQuery].asOpt match {
      case Some(q) =>
        val result = LuceneEngine.generalSearch(q.qs, q.skip, q.limit, q.analyzer, q.strict)
        Ok(JsObject(Seq(
          "data" -> JsArray(result._2.map(_.toJson)),
          "total" -> JsNumber(BigDecimal(result._1))
        )))
      case None => BadRequest(JsObject(Seq("error" -> JsString("Invalid Format"))))
    }
  }
}
