package net.macolabo.sform2.services.Form

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class FormDeleteFormResponse(
                                 id: Option[BigInt]
                                 )

trait FormDeleteFormResponseJson {
  implicit val FormDeleteFormResponseWrites: Writes[FormDeleteFormResponse] = (formDeleteFormResponse: FormDeleteFormResponse) => Json.obj(
    "id" -> formDeleteFormResponse.id
  )
}