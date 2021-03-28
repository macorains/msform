package net.macolabo.sform2.services.User

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import javax.inject.Inject
import net.macolabo.sform2.models
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.UserDAO

import scala.concurrent.{ExecutionContext, Future}

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ex: ExecutionContext) extends UserService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(id: UUID): Future[Option[User]] = userDAO.find(id)

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = userDAO.save(user)

  def save(userSaveRequest: UserSaveRequest, userGroup: String): Future[User] = {
    val userId = userSaveRequest.userId.map(UUID.fromString).getOrElse(UUID.randomUUID())
    save(models.User(
      userID = userId,
      loginInfo = LoginInfo(userId.toString, s"""${userSaveRequest.email}:${userSaveRequest.userGroup}"""),
      group = Option(userGroup),
      role = Option("operator"),
      firstName = Option(userSaveRequest.firstName),
      lastName = Option(userSaveRequest.lastName),
      fullName = Option(userSaveRequest.fullName),
      email = Option(userSaveRequest.email),
      avatarURL = userSaveRequest.avatarUrl,
      activated = userSaveRequest.userId.isDefined,
      deletable = true
    ))
  }
  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save(profile: CommonSocialProfile): Future[User] = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          fullName = profile.fullName,
          email = profile.email,
          avatarURL = profile.avatarURL
        ))
      case None => // Insert a new user
        userDAO.save(models.User(
          userID = UUID.randomUUID(),
          loginInfo = profile.loginInfo,
          group = Option(""),
          role = Option("operator"),
          firstName = profile.firstName,
          lastName = profile.lastName,
          fullName = profile.fullName,
          email = profile.email,
          avatarURL = profile.avatarURL,
          activated = true,
          deletable = true
        ))
    }
  }

  def delete(userId: String, group: String): Unit = {
    userDAO.delete(userId, group)
  }

  /**
   * Adminグループのユーザー存在チェック
   * @return Adminグループのユーザー数が0の場合はfalse、1以上でtrue
   */
  def checkAdminExists: Boolean = {
    !userDAO.countAdminUsers().equals(0)
  }
}
