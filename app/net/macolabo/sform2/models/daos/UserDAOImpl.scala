package net.macolabo.sform2.models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import net.macolabo.sform2.models
import net.macolabo.sform2.models.{SFDBConf, User}
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import scalikejdbc._

import scala.collection.mutable
import scala.concurrent.{Future, _}
import scala.concurrent.duration.Duration

case class UserJson(
                     user_id: String,
                     user_group: String,
                     role: String,
                     first_name: String,
                     last_name: String,
                     full_name: String,
                     email: String,
                     avatar_url: String,
                     activated: Boolean,
                     deletable: Boolean
                   )
object UserJson {
  implicit def jsonUserWrites: Writes[UserJson] = Json.writes[UserJson]
  implicit def jsonUserReads: Reads[UserJson] = Json.reads[UserJson]
}

/**
 * Give access to the user object.
 */
class UserDAOImpl extends UserDAO with SFDBConf {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] =
    Future.successful(
      DB localTx { implicit l =>
        sql"SELECT USER_ID,PROVIDER_ID,PROVIDER_KEY,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE PROVIDER_ID=${loginInfo.providerID} AND PROVIDER_KEY=${loginInfo.providerKey}"
          .map(rs =>
            models.User(
              UUID.fromString(rs.string("USER_ID")),
              LoginInfo(rs.string("PROVIDER_ID"), rs.string("PROVIDER_KEY")),
              Option(rs.string("USER_GROUP")),
              Option(rs.string("ROLE")),
              Option(rs.string("FIRST_NAME")),
              Option(rs.string("LAST_NAME")),
              Option(rs.string("FULL_NAME")),
              Option(rs.string("EMAIL")),
              Option(rs.string("AVATAR_URL")),
              rs.boolean("ACTIVATED"),
              rs.boolean("DELETABLE")
            )
          )
          .single().apply()
      }
    )

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] =
    Future.successful(
      DB localTx { implicit l =>
        sql"SELECT USER_ID,PROVIDER_ID,PROVIDER_KEY,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE USER_ID=${userID.toString}"
          .map(rs =>
            models.User(
              UUID.fromString(rs.string("USER_ID")),
              LoginInfo(rs.string("PROVIDER_ID"), rs.string("PROVIDER_KEY")),
              Option(rs.string("USER_GROUP")),
              Option(rs.string("ROLE")),
              Option(rs.string("FIRST_NAME")),
              Option(rs.string("LAST_NAME")),
              Option(rs.string("FULL_NAME")),
              Option(rs.string("EMAIL")),
              Option(rs.string("AVATAR_URL")),
              rs.boolean("ACTIVATED"),
              rs.boolean("DELETABLE")
            )
          )
          .single().apply()
      }
    )

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    Await.result(find(user.userID), Duration.Inf) match {
      case Some(u: User) => update(user)
      case _ => add(user)
    }
  }

  /**
   * Delete a user.
   * @param userID The ID of the user to delete.
   */
  def delete(userID: String, group: String): Unit = {
    DB localTx { implicit l =>
      sql"DELETE FROM M_USERINFO WHERE USER_ID=$userID AND USER_GROUP=$group".update().apply()
    }
  }

  private def update(user: User): Future[User] = {
    DB localTx { implicit l =>
      sql"UPDATE M_USERINFO SET FIRST_NAME=${user.firstName}, LAST_NAME=${user.lastName} ,EMAIL=${user.email}, AVATAR_URL=${user.avatarURL}, ACTIVATED=${user.activated}, DELETABLE=${user.deletable} WHERE USER_ID=${user.userID.toString}"
        .update().apply()
      Future.successful(user)
    }
  }

  private def add(user: User): Future[User] = {
    DB localTx { implicit l =>
      sql"INSERT INTO M_USERINFO(USER_ID,PROVIDER_ID,PROVIDER_KEY,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE) VALUES(${user.userID.toString},${user.loginInfo.providerID},${user.loginInfo.providerKey},${user.group},${user.role},${user.firstName},${user.lastName},${user.email},'',0,1)"
        .update().apply()
      Future.successful(user)
    }
  }


  def getList(identity: User): JsValue = {
    val userGroup = identity.group.getOrElse("")
    DB localTx { implicit l =>
      val userList = sql"SELECT USER_ID,PROVIDER_ID,PROVIDER_KEY,USER_GROUP,ROLE,FIRST_NAME,LAST_NAME,FULL_NAME,EMAIL,AVATAR_URL,ACTIVATED,DELETABLE FROM M_USERINFO WHERE USER_GROUP=$userGroup"
        .map(rs =>
          models.User(
            UUID.fromString(rs.string("USER_ID")),
            LoginInfo(rs.string("PROVIDER_ID"), rs.string("PROVIDER_KEY")),
            Option(rs.string("USER_GROUP")),
            Option(rs.string("ROLE")),
            Option(rs.string("FIRST_NAME")),
            Option(rs.string("LAST_NAME")),
            Option(rs.string("FULL_NAME")),
            Option(rs.string("EMAIL")),
            Option(rs.string("AVATAR_URL")),
            rs.boolean("ACTIVATED"),
            rs.boolean("DELETABLE")
          )
        )
        .list().apply()
      val userListJson = userList.map(
        u => {
          UserJson(u.userID.toString, u.group.getOrElse(""), u.role.getOrElse(""), u.firstName.getOrElse(""), u.lastName.getOrElse(""),
            u.fullName.getOrElse(""), u.email.getOrElse(""), u.avatarURL.getOrElse(""), u.activated, u.deletable)
        }
      )
      Json.toJson(userListJson)
    }
  }

  // Adminグループのユーザーアカウント数を取得
  def countAdminUsers() :Int = {
    sql"SELECT count(*) FROM M_USERINFO WHERE USER_GROUP='admin'".map(_.int(1)).first().apply().getOrElse(0)
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val users: mutable.HashMap[UUID, User] = mutable.HashMap()
}
