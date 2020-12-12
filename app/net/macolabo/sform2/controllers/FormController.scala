package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import net.macolabo.sform2.services.Form.{FormDeleteFormResponseJson, FormGetFormResponseJson, FormGetListResponseJson, FormInsertFormRequest, FormInsertFormRequestJson, FormInsertFormResponse, FormInsertFormResponseJson, FormService, FormUpdateFormRequest, FormUpdateFormRequestJson, FormUpdateFormResponse, FormUpdateFormResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

class FormController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  formService: FormService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components)
  with I18nSupport
  with FormGetFormResponseJson
  with FormGetListResponseJson
  with FormUpdateFormRequestJson
  with FormUpdateFormResponseJson
  with FormInsertFormRequestJson
  with FormInsertFormResponseJson
  with FormDeleteFormResponseJson
{

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.getForm(request.identity, hashed_form_id)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.getList(request.identity)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォーム更新
   * @return
   */
  def save(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    println(request.body.asJson.get.validate[FormUpdateFormRequest].toString)
    val res = request.body.asJson.flatMap(r =>
      r.validate[FormUpdateFormRequest].map(f => {
        formService.update(request.identity, f)
      }).asOpt)
    res match {
      case Some(s :FormUpdateFormResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
  }

  /**
   * フォーム作成
   * @return
   */
  def create(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = request.body.asJson.flatMap(r =>
      r.validate[FormInsertFormRequest].map(f => {
        formService.insert(request.identity, f)
      }).asOpt)
    res match {
      case Some(s :FormInsertFormResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
  }


  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.deleteForm(request.identity, hashed_form_id)
    res.id match {
      case Some(s: BigInt) => Future.successful(Ok(toJson(res)))
      case None => Future.successful(BadRequest)
    }
  }


}
