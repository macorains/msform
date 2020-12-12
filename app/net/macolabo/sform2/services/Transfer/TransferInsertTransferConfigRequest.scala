package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class TransferInsertTransferRequestMailTransferConfigMailAddress(
                                                                     transfer_config_mail_id: BigInt,
                                                                     address_index: Int,
                                                                     name: String,
                                                                     address: String
                                                                   )
case class TransferInsertTransferRequestMailTransferConfig(
                                                          transfer_config_id: BigInt,
                                                          use_cc: Boolean,
                                                          use_bcc: Boolean,
                                                          use_replyto: Boolean,
                                                          mail_address_list: List[TransferInsertTransferRequestMailTransferConfigMailAddress]
                                                        )

case class TransferInsertTransferRequestSalesforceTransferConfigObject(
                                                                      transfer_config_salesforce_id: BigInt,
                                                                      name: String,
                                                                      label: String,
                                                                      active: Boolean,
                                                                      fields: List[TransferInsertTransferRequestSalesforceTransferConfigObjectField]
                                                                      )

case class TransferInsertTransferRequestSalesforceTransferConfigObjectField(
                                                                             transfer_config_salesforce_object_id: BigInt,
                                                                             name: String,
                                                                             label: String,
                                                                             field_type: String,
                                                                             active: Boolean
                                                                           )

case class TransferInsertTransferRequestSalesforceTransferConfig(
                                                                transfer_config_id: BigInt,
                                                                sf_user_name: String,
                                                                sf_password: String,
                                                                sf_security_token: String,
                                                                objects: List[TransferInsertTransferRequestSalesforceTransferConfigObject]
                                                              )

case class TransferInsertTransferRequestConfigDetail(
                                                    mail: Option[TransferInsertTransferRequestMailTransferConfig],
                                                    salesforce: Option[TransferInsertTransferRequestSalesforceTransferConfig],
                                                  )

case class TransferInsertTransferConfigRequest(
                                              type_code: String,
                                              config_index: Int,
                                              name: String,
                                              status: Int,
                                              detail: TransferInsertTransferRequestConfigDetail,
                                            )


trait TransferInsertTransferConfigRequestJson {
  implicit val TransferInsertTransferRequestMailTransferConfigMailAddressWrites: Writes[TransferInsertTransferRequestMailTransferConfigMailAddress] =
    (transferInsertTransferRequestMailTransferConfigMailAddress:TransferInsertTransferRequestMailTransferConfigMailAddress) =>Json.obj(
      "transfer_config_mail_id" -> transferInsertTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
      "address_index" -> transferInsertTransferRequestMailTransferConfigMailAddress.address_index,
      "name" -> transferInsertTransferRequestMailTransferConfigMailAddress.name,
      "address" -> transferInsertTransferRequestMailTransferConfigMailAddress.address
    )

  implicit val TransferInsertTransferRequestMailTransferConfigMailAddressReads: Reads[TransferInsertTransferRequestMailTransferConfigMailAddress] = (
      (JsPath \ "transfer_config_mail_id").read[BigInt] ~
      (JsPath \ "address_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "address").read[String]
    )(TransferInsertTransferRequestMailTransferConfigMailAddress.apply _)

  implicit val TransferInsertTransferRequestMailTransferConfigWrites: Writes[TransferInsertTransferRequestMailTransferConfig] =
    (transferInsertTransferRequestMailTransferConfig:TransferInsertTransferRequestMailTransferConfig) => Json.obj(
      "transfer_config_id" -> transferInsertTransferRequestMailTransferConfig.transfer_config_id,
      "use_cc" -> transferInsertTransferRequestMailTransferConfig.use_cc,
      "uce_bcc" -> transferInsertTransferRequestMailTransferConfig.use_bcc,
      "use_replyto" -> transferInsertTransferRequestMailTransferConfig.use_replyto,
      "mail_address_list" -> transferInsertTransferRequestMailTransferConfig.mail_address_list
    )

  implicit val TransferInsertTransferRequestMailTransferConfigReads: Reads[TransferInsertTransferRequestMailTransferConfig] = (
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "use_cc").read[Boolean] ~
      (JsPath \ "use_bcc").read[Boolean] ~
      (JsPath \ "use_replyto").read[Boolean] ~
      (JsPath \ "mail_address_list").read[List[TransferInsertTransferRequestMailTransferConfigMailAddress]]
    )(TransferInsertTransferRequestMailTransferConfig.apply _)

  implicit val TransferInsertTransferRequestSalesforceTransferConfigObjectFieldWrites: Writes[TransferInsertTransferRequestSalesforceTransferConfigObjectField] =
    (transferInsertTransferRequestSalesforceTransferConfigObjectField:TransferInsertTransferRequestSalesforceTransferConfigObjectField) => Json.obj(
    "transfer_config_salesforce_object_id" -> transferInsertTransferRequestSalesforceTransferConfigObjectField.transfer_config_salesforce_object_id,
    "name" -> transferInsertTransferRequestSalesforceTransferConfigObjectField.name,
    "label" -> transferInsertTransferRequestSalesforceTransferConfigObjectField.label,
    "field_type" -> transferInsertTransferRequestSalesforceTransferConfigObjectField.field_type,
    "active" -> transferInsertTransferRequestSalesforceTransferConfigObjectField.active
  )

  implicit val TransferInsertTransferRequestSalesforceTransferConfigObjectFieldReads: Reads[TransferInsertTransferRequestSalesforceTransferConfigObjectField] = (
      (JsPath \ "transfer_config_salesforce_object_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "field_type").read[String] ~
      (JsPath \ "active").read[Boolean]
  )(TransferInsertTransferRequestSalesforceTransferConfigObjectField.apply _)

  implicit val TransferInsertTransferRequestSalesforceTransferConfigObjectWrites: Writes[TransferInsertTransferRequestSalesforceTransferConfigObject] =
    (transferInsertTransferRequestSalesforceTransferConfigObject:TransferInsertTransferRequestSalesforceTransferConfigObject) => Json.obj(
      "transfer_config_salesforce_object_id" -> transferInsertTransferRequestSalesforceTransferConfigObject.transfer_config_salesforce_id,
      "name" -> transferInsertTransferRequestSalesforceTransferConfigObject.name,
      "label" -> transferInsertTransferRequestSalesforceTransferConfigObject.label,
      "active" -> transferInsertTransferRequestSalesforceTransferConfigObject.active,
      "fields" -> transferInsertTransferRequestSalesforceTransferConfigObject.fields
    )

  implicit val TransferInsertTransferRequestSalesforceTransferConfigObjectReads: Reads[TransferInsertTransferRequestSalesforceTransferConfigObject] = (
      (JsPath \ "transfer_config_salesforce_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "active").read[Boolean] ~
      (JsPath \ "fields").read[List[TransferInsertTransferRequestSalesforceTransferConfigObjectField]]
    )(TransferInsertTransferRequestSalesforceTransferConfigObject.apply _)


  implicit val TransferInsertTransferRequestSalesforceTransferConfigWrites: Writes[TransferInsertTransferRequestSalesforceTransferConfig] =
    (transferInsertTransferRequestSalesforceTransferConfig: TransferInsertTransferRequestSalesforceTransferConfig) => Json.obj(
      "transfer_config_id" -> transferInsertTransferRequestSalesforceTransferConfig.transfer_config_id,
      "sf_user_name" -> transferInsertTransferRequestSalesforceTransferConfig.sf_user_name,
      "sf_password" -> transferInsertTransferRequestSalesforceTransferConfig.sf_password,
      "sf_security_token" -> transferInsertTransferRequestSalesforceTransferConfig.sf_security_token,
      "objects" -> transferInsertTransferRequestSalesforceTransferConfig.objects
    )

  implicit val TransferInsertTransferRequestSalesforceTransferConfigReads: Reads[TransferInsertTransferRequestSalesforceTransferConfig] = (
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "sf_user_name").read[String] ~
      (JsPath \ "sf_password").read[String] ~
      (JsPath \ "sf_security_token").read[String] ~
      (JsPath \ "objects").read[List[TransferInsertTransferRequestSalesforceTransferConfigObject]]
    )(TransferInsertTransferRequestSalesforceTransferConfig.apply _)

  implicit val TransferInsertTransferRequestConfigDetailWrites: Writes[TransferInsertTransferRequestConfigDetail] = (transferInsertTransferRequestConfigDetail: TransferInsertTransferRequestConfigDetail) => Json.obj(
    "mail" -> transferInsertTransferRequestConfigDetail.mail,
    "salesforce" -> transferInsertTransferRequestConfigDetail.salesforce
  )

  implicit val transferInsertTransferRequestConfigDetailReads: Reads[TransferInsertTransferRequestConfigDetail] = (
    (JsPath \ "mail").readNullable[TransferInsertTransferRequestMailTransferConfig] ~
      (JsPath \ "salesforce").readNullable[TransferInsertTransferRequestSalesforceTransferConfig]
    )(TransferInsertTransferRequestConfigDetail.apply _)

  implicit val TransferInsertTransferRequestWrites: Writes[TransferInsertTransferConfigRequest] = (transferInsertTransferRequest: TransferInsertTransferConfigRequest) => Json.obj(
    "type_code" -> transferInsertTransferRequest.type_code,
    "config_index" -> transferInsertTransferRequest.config_index,
    "name" -> transferInsertTransferRequest.name,
    "status" -> transferInsertTransferRequest.status,
    "detail" -> transferInsertTransferRequest.detail
  )

  implicit val TransferInsertTransferRequestReads: Reads[TransferInsertTransferConfigRequest] = (
      (JsPath \ "type_code").read[String] ~
      (JsPath \ "config_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "detail").read[TransferInsertTransferRequestConfigDetail]
    )(TransferInsertTransferConfigRequest.apply _)
}