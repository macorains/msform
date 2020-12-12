package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class TransferUpdateTransferRequestMailTransferConfigMailAddress(
                                                                     id: Option[BigInt],
                                                                     transfer_config_mail_id: BigInt,
                                                                     address_index: Int,
                                                                     name: String,
                                                                     address: String
                                                                   )
case class TransferUpdateTransferRequestMailTransferConfig(
                                                          id: BigInt,
                                                          transfer_config_id: BigInt,
                                                          use_cc: Boolean,
                                                          use_bcc: Boolean,
                                                          use_replyto: Boolean,
                                                          mail_address_list: List[TransferUpdateTransferRequestMailTransferConfigMailAddress]
                                                        )

case class TransferUpdateTransferRequestSalesforceTransferConfigObject(
                                                                      id: Option[BigInt],
                                                                      transfer_config_salesforce_id: BigInt,
                                                                      name: String,
                                                                      label: String,
                                                                      active: Boolean,
                                                                      fields: List[TransferUpdateTransferRequestSalesforceTransferConfigObjectField]
                                                                      )

case class TransferUpdateTransferRequestSalesforceTransferConfigObjectField(
                                                                             id: Option[BigInt],
                                                                             transfer_config_salesforce_object_id: BigInt,
                                                                             name: String,
                                                                             label: String,
                                                                             field_type: String,
                                                                             active: Boolean
                                                                           )

case class TransferUpdateTransferRequestSalesforceTransferConfig(
                                                                id: BigInt,
                                                                transfer_config_id: BigInt,
                                                                sf_user_name: String,
                                                                sf_password: String,
                                                                sf_security_token: String,
                                                                objects: List[TransferUpdateTransferRequestSalesforceTransferConfigObject]
                                                              )

case class TransferUpdateTransferRequestConfigDetail(
                                                    mail: Option[TransferUpdateTransferRequestMailTransferConfig],
                                                    salesforce: Option[TransferUpdateTransferRequestSalesforceTransferConfig],
                                                  )

case class TransferUpdateTransferConfigRequest(
                                              id: BigInt,
                                              type_code: String,
                                              config_index: Int,
                                              name: String,
                                              status: Int,
                                              detail: TransferUpdateTransferRequestConfigDetail,
                                            )


trait TransferUpdateTransferConfigRequestJson {
  implicit val TransferUpdateTransferRequestMailTransferConfigMailAddressWrites: Writes[TransferUpdateTransferRequestMailTransferConfigMailAddress] =
    (transferUpdateTransferRequestMailTransferConfigMailAddress:TransferUpdateTransferRequestMailTransferConfigMailAddress) =>Json.obj(
      "id" -> transferUpdateTransferRequestMailTransferConfigMailAddress.id,
      "transfer_config_mail_id" -> transferUpdateTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
      "address_index" -> transferUpdateTransferRequestMailTransferConfigMailAddress.address_index,
      "name" -> transferUpdateTransferRequestMailTransferConfigMailAddress.name,
      "address" -> transferUpdateTransferRequestMailTransferConfigMailAddress.address
    )

  implicit val TransferUpdateTransferRequestMailTransferConfigMailAddressReads: Reads[TransferUpdateTransferRequestMailTransferConfigMailAddress] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "transfer_config_mail_id").read[BigInt] ~
      (JsPath \ "address_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "address").read[String]
    )(TransferUpdateTransferRequestMailTransferConfigMailAddress.apply _)

  implicit val TransferUpdateTransferRequestMailTransferConfigWrites: Writes[TransferUpdateTransferRequestMailTransferConfig] =
    (transferUpdateTransferRequestMailTransferConfig:TransferUpdateTransferRequestMailTransferConfig) => Json.obj(
      "id" -> transferUpdateTransferRequestMailTransferConfig.id,
      "transfer_config_id" -> transferUpdateTransferRequestMailTransferConfig.transfer_config_id,
      "use_cc" -> transferUpdateTransferRequestMailTransferConfig.use_cc,
      "uce_bcc" -> transferUpdateTransferRequestMailTransferConfig.use_bcc,
      "use_replyto" -> transferUpdateTransferRequestMailTransferConfig.use_replyto,
      "mail_address_list" -> transferUpdateTransferRequestMailTransferConfig.mail_address_list
    )

  implicit val TransferUpdateTransferRequestMailTransferConfigReads: Reads[TransferUpdateTransferRequestMailTransferConfig] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "use_cc").read[Boolean] ~
      (JsPath \ "use_bcc").read[Boolean] ~
      (JsPath \ "use_replyto").read[Boolean] ~
      (JsPath \ "mail_address_list").read[List[TransferUpdateTransferRequestMailTransferConfigMailAddress]]
    )(TransferUpdateTransferRequestMailTransferConfig.apply _)

  implicit val TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldWrites: Writes[TransferUpdateTransferRequestSalesforceTransferConfigObjectField] =
    (transferUpdateTransferRequestSalesforceTransferConfigObjectField:TransferUpdateTransferRequestSalesforceTransferConfigObjectField) => Json.obj(
      "id" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.id,
    "transfer_config_salesforce_object_id" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.transfer_config_salesforce_object_id,
    "name" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.name,
    "label" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.label,
    "field_type" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.field_type,
    "active" -> transferUpdateTransferRequestSalesforceTransferConfigObjectField.active
  )

  implicit val TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldReads: Reads[TransferUpdateTransferRequestSalesforceTransferConfigObjectField] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "transfer_config_salesforce_object_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "field_type").read[String] ~
      (JsPath \ "active").read[Boolean]
  )(TransferUpdateTransferRequestSalesforceTransferConfigObjectField.apply _)

  implicit val TransferUpdateTransferRequestSalesforceTransferConfigObjectWrites: Writes[TransferUpdateTransferRequestSalesforceTransferConfigObject] =
    (transferUpdateTransferRequestSalesforceTransferConfigObject:TransferUpdateTransferRequestSalesforceTransferConfigObject) => Json.obj(
      "id" -> transferUpdateTransferRequestSalesforceTransferConfigObject.id,
      "transfer_config_salesforce_object_id" -> transferUpdateTransferRequestSalesforceTransferConfigObject.transfer_config_salesforce_id,
      "name" -> transferUpdateTransferRequestSalesforceTransferConfigObject.name,
      "label" -> transferUpdateTransferRequestSalesforceTransferConfigObject.label,
      "active" -> transferUpdateTransferRequestSalesforceTransferConfigObject.active,
      "fields" -> transferUpdateTransferRequestSalesforceTransferConfigObject.fields
    )

  implicit val TransferUpdateTransferRequestSalesforceTransferConfigObjectReads: Reads[TransferUpdateTransferRequestSalesforceTransferConfigObject] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "transfer_config_salesforce_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "active").read[Boolean] ~
      (JsPath \ "fields").read[List[TransferUpdateTransferRequestSalesforceTransferConfigObjectField]]
    )(TransferUpdateTransferRequestSalesforceTransferConfigObject.apply _)


  implicit val TransferUpdateTransferRequestSalesforceTransferConfigWrites: Writes[TransferUpdateTransferRequestSalesforceTransferConfig] =
    (transferUpdateTransferRequestSalesforceTransferConfig: TransferUpdateTransferRequestSalesforceTransferConfig) => Json.obj(
      "id" -> transferUpdateTransferRequestSalesforceTransferConfig.id,
      "transfer_config_id" -> transferUpdateTransferRequestSalesforceTransferConfig.transfer_config_id,
      "sf_user_name" -> transferUpdateTransferRequestSalesforceTransferConfig.sf_user_name,
      "sf_password" -> transferUpdateTransferRequestSalesforceTransferConfig.sf_password,
      "sf_security_token" -> transferUpdateTransferRequestSalesforceTransferConfig.sf_security_token,
      "objects" -> transferUpdateTransferRequestSalesforceTransferConfig.objects
    )

  implicit val TransferUpdateTransferRequestSalesforceTransferConfigReads: Reads[TransferUpdateTransferRequestSalesforceTransferConfig] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "sf_user_name").read[String] ~
      (JsPath \ "sf_password").read[String] ~
      (JsPath \ "sf_security_token").read[String] ~
      (JsPath \ "objects").read[List[TransferUpdateTransferRequestSalesforceTransferConfigObject]]
    )(TransferUpdateTransferRequestSalesforceTransferConfig.apply _)

  implicit val TransferUpdateTransferRequestConfigDetailWrites: Writes[TransferUpdateTransferRequestConfigDetail] = (transferUpdateTransferRequestConfigDetail: TransferUpdateTransferRequestConfigDetail) => Json.obj(
    "mail" -> transferUpdateTransferRequestConfigDetail.mail,
    "salesforce" -> transferUpdateTransferRequestConfigDetail.salesforce
  )

  implicit val transferUpdateTransferRequestConfigDetailReads: Reads[TransferUpdateTransferRequestConfigDetail] = (
    (JsPath \ "mail").readNullable[TransferUpdateTransferRequestMailTransferConfig] ~
      (JsPath \ "salesforce").readNullable[TransferUpdateTransferRequestSalesforceTransferConfig]
    )(TransferUpdateTransferRequestConfigDetail.apply _)

  implicit val TransferUpdateTransferRequestWrites: Writes[TransferUpdateTransferConfigRequest] = (transferUpdateTransferRequest: TransferUpdateTransferConfigRequest) => Json.obj(
    "id" -> transferUpdateTransferRequest.id,
    "type_code" -> transferUpdateTransferRequest.type_code,
    "config_index" -> transferUpdateTransferRequest.config_index,
    "name" -> transferUpdateTransferRequest.name,
    "status" -> transferUpdateTransferRequest.status,
    "detail" -> transferUpdateTransferRequest.detail
  )

  implicit val TransferUpdateTransferRequestReads: Reads[TransferUpdateTransferConfigRequest] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "type_code").read[String] ~
      (JsPath \ "config_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "detail").read[TransferUpdateTransferRequestConfigDetail]
    )(TransferUpdateTransferConfigRequest.apply _)
}