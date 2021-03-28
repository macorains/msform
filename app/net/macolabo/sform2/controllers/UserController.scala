package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import net.macolabo.sform2.models.RsResultSet
import net.macolabo.sform2.models.daos.UserDAO
import net.macolabo.sform2.services.User.{UserSaveRequest, UserSaveRequestJson, UserService}
import org.webjars.play.WebJarsUtil
import play.api.{Environment, _}
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject() (
  env: Environment,
  dbapi: DBApi,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  userDAO: UserDAO
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with UserSaveRequestJson {

  // ToDo グループによる制御必要
  // GET /user
  def getList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async { implicit request =>
    val res = RsResultSet("OK", "OK", userDAO.getList(request.identity))
    Future.successful(Ok(Json.toJson(res)))
  }

  // adminロールの有無チェック用
  // GET /user/isadmin
  def isAdmin: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async { implicit request =>
    val res = RsResultSet("OK", "OK", Json.toJson(""))
    Future.successful(Ok(Json.toJson(res)))
  }

  // ユーザーの保存
  // POST /user
  def save: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async { implicit request =>
    request.body.asJson.flatMap(bodyJson => {
      bodyJson.validate[UserSaveRequest].asOpt.map(userSaveRequest => {
        userService.save(userSaveRequest, request.identity.group.getOrElse(""))
        Future.successful(Ok)
      })
    }).getOrElse(Future.successful(BadRequest))
  }

  // ユーザーの削除
  // DELETE /user
  def delete(userId: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async { implicit request =>
    userService.delete(userId, request.identity.group.getOrElse(""))
    val res = RsResultSet("OK", "OK", Json.toJson(""))
    Future.successful(Ok(Json.toJson(res)))
  }
}
