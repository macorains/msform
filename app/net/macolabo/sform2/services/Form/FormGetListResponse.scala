package net.macolabo.sform2.services.Form

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class FormGetListForm(
                            id: BigInt,
                            name: String,
                            form_index: Int,
                            title: String,
                            status: Int,
                            hashed_id: String,
                          )
case class FormGetListResponse (
                               forms: List[FormGetListForm],
                               data_count: Int
                               )

trait FormGetListResponseJson {
  implicit val FormGetListFormWrites: Writes[FormGetListForm] = (formGetListForm: FormGetListForm) => Json.obj(
    "id" -> formGetListForm.id,
    "name" -> formGetListForm.name,
    "form_index" -> formGetListForm.form_index,
    "title" -> formGetListForm.title,
    "status" -> formGetListForm.status,
    "hashed_id" -> formGetListForm.hashed_id
  )
  implicit val FormGetListFormReads: Reads[FormGetListForm] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "hashed_id").read[String]
  )(FormGetListForm.apply _)
  implicit val FormGetListResponseWrites: Writes[FormGetListResponse] = (formGetListResponse: FormGetListResponse) => Json.obj(
    "forms" -> formGetListResponse.forms,
    "data_count" -> formGetListResponse.data_count
  )
  implicit val FormGetListResponseReads: Reads[FormGetListResponse] = (
    (JsPath \ "forms").read[List[FormGetListForm]] ~
      (JsPath \ "data_count").read[Int]
  )(FormGetListResponse.apply _)
}
