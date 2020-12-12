package net.macolabo.sform2.services.Form

import play.api.libs.json.{Json, Writes}

case class FormUpdateFormResponse(
                                 id: BigInt
                                 )

trait FormUpdateFormResponseJson {
  implicit val FormUpdateFormResponseWrites: Writes[FormUpdateFormResponse] = (formUpdateFormResponse: FormUpdateFormResponse) => Json.obj(
    "id"-> formUpdateFormResponse.id
  )
}


