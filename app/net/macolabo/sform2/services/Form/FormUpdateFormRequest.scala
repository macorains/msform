package net.macolabo.sform2.services.Form

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import net.macolabo.sform2.utils.StringUtils.StringImprovements

/**
 * フォーム更新API・フォーム項目・バリデーション
 * @param id ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param max_value 最大値
 * @param min_value 最小値
 * @param max_length 最大長
 * @param min_length 最小長
 * @param input_type 入力種別
 */
case class FormUpdateFormRequestFormColValidation(
                                                 id: Option[BigInt],
                                                 form_col_id: Option[BigInt],
                                                 form_id: BigInt,
                                                 max_value: Option[Int],
                                                 min_value: Option[Int],
                                                 max_length: Option[Int],
                                                 min_length: Option[Int],
                                                 input_type: Int,
                                                 required: Boolean
                                               )

/**
 * フォーム更新API・フォーム項目・選択リスト
 * @param id ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param select_index 順番
 * @param select_name 表示テキスト
 * @param select_value 値　
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時CSSスタイル
 * @param view_style 参照時CSSスタイル
 */
case class FormUpdateFormRequestFormColSelect(
                                             id: Option[BigInt],
                                             form_col_id: Option[BigInt],
                                             form_id: BigInt,
                                             select_index: Int,
                                             select_name: String,
                                             select_value: String,
                                             is_default: Boolean,
                                             edit_style: String,
                                             view_style: String
                                           )

/**
 * フォーム更新API・フォーム項目
 * @param id ID
 * @param form_id フォームID
 * @param name 項目名
 * @param col_id 項目ID
 * @param col_index 順番
 * @param col_type 項目種別
 * @param default_value 初期値
 * @param select_list 選択リスト
 * @param validations バリデーション
 */
case class FormUpdateFormRequestFormCol(
                                       id: Option[BigInt],
                                       form_id: BigInt,
                                       name: String,
                                       col_id: String,
                                       col_index: Int,
                                       col_type: Int,
                                       default_value: String,
                                       select_list: List[FormUpdateFormRequestFormColSelect],
                                       validations: FormUpdateFormRequestFormColValidation
                                     )

/**
 * フォーム更新API・FormTransferTask・Salesforce・Field
 * @param id FormTransferTaskSalesforceField ID
 * @param form_transfer_task_salesforce_id FormTransferTaskSalesforce ID
 * @param form_column_id フォーム項目ID
 * @param field_name Salesforceフィールド名
 */
case class FormUpdateFormRequestFormTransferTaskSalesforceField(
                                                            id: Option[BigInt],
                                                            form_transfer_task_salesforce_id: Int,
                                                            form_column_id: String,
                                                            field_name: String
                                                          )

/**
 * フォーム更新API・FormTransferTask・Condition
 * @param id FormTransferTaskCondition ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param form_id フォームID
 * @param form_col_id フォーム項目ID
 * @param operator 演算子
 * @param cond_value 値　
 */
case class FormUpdateFormRequestFormTransferTaskCondition(
                                                           id: Option[BigInt],
                                                           form_transfer_task_id: BigInt,
                                                           form_id: BigInt,
                                                           form_col_id: BigInt,
                                                           operator: String,
                                                           cond_value: String
                                                )

/**
 * フォーム更新API・FormTransferTask・Mail
 * @param id ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param from_address_id FROMに使うメールアドレスのID
 * @param to_address Toアドレス
 * @param cc_address Ccアドレス
 * @param bcc_address_id Bccに使うメールアドレスのID
 * @param replyto_address_id replyToに使うメールアドレスのID
 * @param subject 件名
 * @param body 本文
 */
case class FormUpdateFormRequestFormTransferTaskMail(
                                                      id: Option[BigInt],
                                                      form_transfer_task_id: Option[BigInt],
                                                      from_address_id: BigInt,
                                                      to_address: String,
                                                      cc_address: String,
                                                      bcc_address_id: BigInt,
                                                      replyto_address_id: BigInt,
                                                      subject: String,
                                                      body: String
                                                    )

/**
 * フォーム更新API・FormTransferTask・Salesforce
 * @param id ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param object_name Salesforceオブジェクト名
 * @param fields フィールド割り当て情報
 */
case class FormUpdateFormRequestFormTransferTaskSalesforce(
                                                            id: Option[BigInt],
                                                            form_transfer_task_id: Option[BigInt],
                                                            object_name: String,
                                                            fields: List[FormUpdateFormRequestFormTransferTaskSalesforceField]
                                                          )


/**
 * フォーム更新API・FormTransferTask
 * @param id ID
 * @param transfer_config_id TransferConfig ID
 * @param form_id Form ID
 * @param task_index 順番
 * @param name 名前
 * @param form_transfer_task_conditions 実行条件データ
 * @param mail MailTransfer設定
 * @param salesforce SalesforceTransfer設定
 */
case class FormUpdateFormRequestFormTransferTask(
                                                  id: Option[BigInt],
                                                  transfer_config_id: BigInt,
                                                  form_id: BigInt,
                                                  task_index: Int,
                                                  name: String,
                                                  form_transfer_task_conditions: List[FormUpdateFormRequestFormTransferTaskCondition],
                                                  mail: Option[FormUpdateFormRequestFormTransferTaskMail],
                                                  salesforce: Option[FormUpdateFormRequestFormTransferTaskSalesforce]
                                                )

/**
 * フォーム更新API・フォームデータ
 * @param id フォームID
 * @param name フォーム名
 * @param form_index 順番
 * @param title タイトル
 * @param status ステータス
 * @param cancel_url キャンセル時遷移先URL
 * @param close_text フォームクローズ時文言
 * @param hashed_id ハッシュ化フォームID
 * @param complete_url 完了時遷移先URL
 * @param input_header 入力画面のヘッダ文言
 * @param complete_text 完了時の文言
 * @param confirm_header 確認画面のヘッダ文言
 * @param form_cols フォーム項目
 */
case class FormUpdateFormRequest(
                                id: BigInt,
                                name: String,
                                form_index: Int,
                                title: String,
                                status: Int,
                                cancel_url: String,
                                close_text: String,
                                hashed_id: String,
                                complete_url: String,
                                input_header: String,
                                complete_text: String,
                                confirm_header: String,
                                form_cols: List[FormUpdateFormRequestFormCol],
                                form_transfer_tasks: List[FormUpdateFormRequestFormTransferTask]
                              ) {

}

trait FormUpdateFormRequestJson {
  implicit val FormUpdateFormRequestFormTransferTaskSalesforceFieldWrites: Writes[FormUpdateFormRequestFormTransferTaskSalesforceField] = (formUpdateFormRequestFormTransferTaskSalesforceField:FormUpdateFormRequestFormTransferTaskSalesforceField) => Json.obj(
    "id" -> formUpdateFormRequestFormTransferTaskSalesforceField.id,
    "form_transfer_task_salesforce_id" -> formUpdateFormRequestFormTransferTaskSalesforceField.form_transfer_task_salesforce_id,
    "form_column_id" -> formUpdateFormRequestFormTransferTaskSalesforceField.form_column_id,
    "field_name" -> formUpdateFormRequestFormTransferTaskSalesforceField.field_name
  )

  implicit val FormUpdateFormRequestFormTransferTaskSalesforceFieldReads: Reads[FormUpdateFormRequestFormTransferTaskSalesforceField] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_salesforce_id").read[Int] ~
      (JsPath \ "form_column_id").read[String] ~
      (JsPath \ "field_name").read[String]
  )(FormUpdateFormRequestFormTransferTaskSalesforceField.apply _)

  implicit val FormUpdateFormRequestFormTransferTaskConditionWrites: Writes[FormUpdateFormRequestFormTransferTaskCondition] = (formUpdateFormRequestFormTransferTaskCondition:FormUpdateFormRequestFormTransferTaskCondition) => Json.obj(
    "id" -> formUpdateFormRequestFormTransferTaskCondition.id,
    "form_transfer_task_id" -> formUpdateFormRequestFormTransferTaskCondition.form_transfer_task_id,
    "form_id" -> formUpdateFormRequestFormTransferTaskCondition.form_id,
    "form_col_id" -> formUpdateFormRequestFormTransferTaskCondition.form_col_id,
    "operator" -> formUpdateFormRequestFormTransferTaskCondition.operator,
    "cond_value" -> formUpdateFormRequestFormTransferTaskCondition.cond_value
  )

  implicit val FormUpdateFormRequestFormTransferTaskConditionReads: Reads[FormUpdateFormRequestFormTransferTaskCondition] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "form_col_id").read[BigInt] ~
      (JsPath \ "operator").read[String] ~
      (JsPath \ "cond_value").read[String]
  )(FormUpdateFormRequestFormTransferTaskCondition.apply _)

  implicit val FormUpdateFormRequestFormTransferTaskMailWrites: Writes[FormUpdateFormRequestFormTransferTaskMail] = (formUpdateFormRequestFormTransferTaskMail:FormUpdateFormRequestFormTransferTaskMail) => Json.obj(
    "id" -> formUpdateFormRequestFormTransferTaskMail.id,
    "form_transfer_task_id" -> formUpdateFormRequestFormTransferTaskMail.form_transfer_task_id,
    "from_address_id" -> formUpdateFormRequestFormTransferTaskMail.from_address_id,
    "to_address" -> formUpdateFormRequestFormTransferTaskMail.to_address,
    "cc_address" -> formUpdateFormRequestFormTransferTaskMail.cc_address,
    "bcc_address_id" -> formUpdateFormRequestFormTransferTaskMail.bcc_address_id,
    "replyto_address_id" -> formUpdateFormRequestFormTransferTaskMail.replyto_address_id,
    "subject" -> formUpdateFormRequestFormTransferTaskMail.subject,
    "body" -> formUpdateFormRequestFormTransferTaskMail.body
  )

  implicit val FormUpdateFormRequestFormTransferTaskMailReads: Reads[FormUpdateFormRequestFormTransferTaskMail] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").readNullable[BigInt] ~
      (JsPath \ "from_address_id").read[BigInt] ~
      (JsPath \ "to_address").read[String] ~
      (JsPath \ "cc_address").read[String] ~
      (JsPath \ "bcc_address_id").read[BigInt] ~
      (JsPath \ "replyto_address_id").read[BigInt] ~
      (JsPath \ "subject").read[String] ~
      (JsPath \ "body").read[String]
  )(FormUpdateFormRequestFormTransferTaskMail.apply _)

  implicit val FormUpdateFormRequestFormTransferTaskSalesforceWrites: Writes[FormUpdateFormRequestFormTransferTaskSalesforce] = (formUpdateFormRequestFormTransferTaskSalesforce:FormUpdateFormRequestFormTransferTaskSalesforce) => Json.obj(
    "id" -> formUpdateFormRequestFormTransferTaskSalesforce.id,
    "form_transfer_task_id" -> formUpdateFormRequestFormTransferTaskSalesforce.form_transfer_task_id,
    "object_name" -> formUpdateFormRequestFormTransferTaskSalesforce.object_name,
    "fields" -> formUpdateFormRequestFormTransferTaskSalesforce.fields
  )

  implicit val FormUpdateFormRequestFormTransferTaskSalesforceReads: Reads[FormUpdateFormRequestFormTransferTaskSalesforce] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").readNullable[BigInt] ~
      (JsPath \ "object_name").read[String] ~
      (JsPath \ "fields").read[List[FormUpdateFormRequestFormTransferTaskSalesforceField]]
  )(FormUpdateFormRequestFormTransferTaskSalesforce.apply _)

  implicit val FormUpdateFormRequestFormTransferTaskWrites: Writes[FormUpdateFormRequestFormTransferTask] = (formUpdateFormRequestFormTransferTask:FormUpdateFormRequestFormTransferTask) => Json.obj(
    "id" -> formUpdateFormRequestFormTransferTask.id,
    "transfer_config_id" -> formUpdateFormRequestFormTransferTask.transfer_config_id,
    "form_id" -> formUpdateFormRequestFormTransferTask.form_id,
    "task_index" -> formUpdateFormRequestFormTransferTask.task_index,
    "name" -> formUpdateFormRequestFormTransferTask.name,
    "form_transfer_task_conditions" -> formUpdateFormRequestFormTransferTask.form_transfer_task_conditions,
    "mail" -> formUpdateFormRequestFormTransferTask.mail,
    "salesforce" -> formUpdateFormRequestFormTransferTask.salesforce
  )

  implicit val FormUpdateFormRequestFormTransferTaskReads: Reads[FormUpdateFormRequestFormTransferTask] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "task_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_transfer_task_conditions").read[List[FormUpdateFormRequestFormTransferTaskCondition]] ~
      (JsPath \ "mail").readNullable[FormUpdateFormRequestFormTransferTaskMail] ~
      (JsPath \ "salesforce").readNullable[FormUpdateFormRequestFormTransferTaskSalesforce]
  )(FormUpdateFormRequestFormTransferTask.apply _)

  implicit val FormUpdateFormRequestFormColValidationWrites: Writes[FormUpdateFormRequestFormColValidation] = (formUpdateFormRequestFormColValidation: FormUpdateFormRequestFormColValidation) => Json.obj(
    "id" -> formUpdateFormRequestFormColValidation.id,
    "form_col_id" -> formUpdateFormRequestFormColValidation.form_col_id,
    "form_id" -> formUpdateFormRequestFormColValidation.form_id,
    "max_value" -> formUpdateFormRequestFormColValidation.max_value,
    "min_value" -> formUpdateFormRequestFormColValidation.min_value,
    "max_length" -> formUpdateFormRequestFormColValidation.max_length,
    "min_length" -> formUpdateFormRequestFormColValidation.min_length,
    "input_type" -> formUpdateFormRequestFormColValidation.input_type
  )

  implicit val FormUpdateFormRequestFormColValidationReads: Reads[FormUpdateFormRequestFormColValidation] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_col_id").readNullable[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "max_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "max_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "input_type").read[Int] ~
      (JsPath \ "required").read[Boolean]
    )(FormUpdateFormRequestFormColValidation.apply _)

  implicit val FormUpdateFormRequestFormColSelectListWrites: Writes[FormUpdateFormRequestFormColSelect] = (FormUpdateFormRequestFormColSelect: FormUpdateFormRequestFormColSelect) => Json.obj(
    "id" -> FormUpdateFormRequestFormColSelect.id,
    "form_col_id" -> FormUpdateFormRequestFormColSelect.form_col_id,
    "form_id" -> FormUpdateFormRequestFormColSelect.form_id,
    "select_index" -> FormUpdateFormRequestFormColSelect.select_index,
    "select_name" -> FormUpdateFormRequestFormColSelect.select_name,
    "select_value" -> FormUpdateFormRequestFormColSelect.select_value,
    "is_default" -> FormUpdateFormRequestFormColSelect.is_default,
    "edit_style" -> FormUpdateFormRequestFormColSelect.edit_style,
    "view_style" -> FormUpdateFormRequestFormColSelect.view_style
  )

  implicit val FormUpdateFormRequestFormColSelectListReads: Reads[FormUpdateFormRequestFormColSelect] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_col_id").readNullable[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormUpdateFormRequestFormColSelect.apply _)

  implicit val FormUpdateFormRequestFormColWrites: Writes[FormUpdateFormRequestFormCol] = (formUpdateFormRequestFormCol: FormUpdateFormRequestFormCol) => Json.obj(
    "id" -> formUpdateFormRequestFormCol.id,
    "form_id" -> formUpdateFormRequestFormCol.form_id,
    "name" -> formUpdateFormRequestFormCol.name,
    "col_id" -> formUpdateFormRequestFormCol.col_id,
    "col_index" -> formUpdateFormRequestFormCol.col_index,
    "col_type" -> formUpdateFormRequestFormCol.col_type,
    "default_value" -> formUpdateFormRequestFormCol.default_value,
    "select_list" -> formUpdateFormRequestFormCol.select_list,
    "validations" -> formUpdateFormRequestFormCol.validations
  )

  implicit val FormUpdateFormRequestFormColReads: Reads[FormUpdateFormRequestFormCol] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormUpdateFormRequestFormColSelect]] ~
      (JsPath \ "validations").read[FormUpdateFormRequestFormColValidation]
    )(FormUpdateFormRequestFormCol.apply _)

  implicit val FormUpdateFormRequestWrites: Writes[FormUpdateFormRequest] = (formUpdateFormRequest: FormUpdateFormRequest) => Json.obj(
    "id" -> formUpdateFormRequest.id,
    "name" -> formUpdateFormRequest.name,
    "form_index" -> formUpdateFormRequest.form_index,
    "title" -> formUpdateFormRequest.title,
    "status" -> formUpdateFormRequest.status,
    "cancel_url" -> formUpdateFormRequest.cancel_url,
    "close_text" -> formUpdateFormRequest.close_text,
    "hashed_id" -> formUpdateFormRequest.hashed_id,
    "complete_url" -> formUpdateFormRequest.complete_url,
    "input_header" -> formUpdateFormRequest.input_header,
    "complete_text" -> formUpdateFormRequest.complete_text,
    "confirm_header" -> formUpdateFormRequest.confirm_header,
    "form_cols" -> formUpdateFormRequest.form_cols
  )

  implicit val FormUpdateFormRequestReads: Reads[FormUpdateFormRequest] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "cancel_url").read[String] ~
      (JsPath \ "close_text").read[String] ~
      (JsPath \ "hashed_id").read[String] ~
      (JsPath \ "complete_url").read[String] ~
      (JsPath \ "input_header").read[String] ~
      (JsPath \ "complete_text").read[String] ~
      (JsPath \ "confirm_header").read[String] ~
      (JsPath \ "form_cols").read[List[FormUpdateFormRequestFormCol]] ~
      (JsPath \ "form_transfer_tasks").read[List[FormUpdateFormRequestFormTransferTask]]
    )(FormUpdateFormRequest.apply _)
}