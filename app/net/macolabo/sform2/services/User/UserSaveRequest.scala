package net.macolabo.sform2.services.User

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class UserSaveRequest (
                             userId: Option[String],
                             userGroup: String,
                             role: String,
                             firstName: String,
                             lastName: String,
                             fullName: String,
                             email: String,
                             avatarUrl: Option[String]
                           )

trait UserSaveRequestJson {
  implicit val UesrSaveRequestReads: Reads[UserSaveRequest] = (
    (JsPath \ "user_id").readNullable[String] ~
      (JsPath \ "user_group").read[String] ~
      (JsPath \ "role").read[String] ~
      (JsPath \ "first_name").read[String] ~
      (JsPath \ "last_name").read[String] ~
      (JsPath \ "full_name").read[String] ~
      (JsPath \ "email").read[String] ~
      (JsPath \ "avatar_url").readNullable[String]
  )(UserSaveRequest.apply _)
}
