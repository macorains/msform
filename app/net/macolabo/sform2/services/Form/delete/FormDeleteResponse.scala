package net.macolabo.sform2.services.Form.delete

import play.api.libs.json.{Json, Writes}

case class FormDeleteResponse(
                               id: Option[BigInt]
                             )

trait FormDeleteFormResponseJson {
  implicit val FormDeleteResponseWrites: Writes[FormDeleteResponse] = (formDeleteResponse: FormDeleteResponse) => Json.obj(
    "id" -> formDeleteResponse.id
  )
}