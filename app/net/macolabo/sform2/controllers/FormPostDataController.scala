package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import net.macolabo.sform2.models.daos.PostdataDAO
import net.macolabo.sform2.services.FormPostData.FormPostDataService
import org.webjars.play.WebJarsUtil
import play.api.Environment
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import net.macolabo.sform2.utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class FormPostDataController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv]
                                       )(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext,
  formPostDataService: FormPostDataService
) extends AbstractController(components) with I18nSupport {

  case class postDataResponse(total: String, page: String, records: String, cols: JsValue, rows: JsValue)
  object postDataResponse {
    implicit def jsonPostDataResponseWrites: Writes[postDataResponse] = Json.writes[postDataResponse]
    implicit def jsonPostDataResponseReads: Reads[postDataResponse] = Json.reads[postDataResponse]
  }

  def getPostData(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(formPostDataService.getData(hashed_form_id, request.identity)).as("application/json; charset=UTF-8"))
  }

  def addPostDataId(id: Int, postdata: JsValue): JsValue = {
    Json.toJson(postdata.as[JsObject] ++ Json.obj("id" -> id.toString))
  }
}
