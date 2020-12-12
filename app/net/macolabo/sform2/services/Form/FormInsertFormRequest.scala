package net.macolabo.sform2.services.Form
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import net.macolabo.sform2.utils.StringUtils.StringImprovements

/**
 * フォーム作成API・フォーム項目・バリデーション
 * @param max_value 最大値
 * @param min_value 最小値
 * @param max_length 最大長
 * @param min_length 最小長
 * @param input_type 入力種別
 * @param required 必須項目
 */
case class FormInsertFormRequestFormColValidation(
                                                   max_value: Option[Int],
                                                   min_value: Option[Int],
                                                   max_length: Option[Int],
                                                   min_length: Option[Int],
                                                   input_type: Int,
                                                   required: Boolean
                                                 )

/**
 * フォーム作成API・フォーム項目・選択リスト
 * @param select_index 順番
 * @param select_name 表示テキスト
 * @param select_value 値　
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時CSSスタイル
 * @param view_style 参照時CSSスタイル
 */
case class FormInsertFormRequestFormColSelect(
                                               select_index: Int,
                                               select_name: String,
                                               select_value: String,
                                               is_default: Boolean,
                                               edit_style: String,
                                               view_style: String
                                             )

/**
 * フォーム作成API・フォーム項目
 * @param name 項目名
 * @param col_id 項目ID
 * @param col_index 順番
 * @param col_type 項目種別
 * @param default_value 初期値
 * @param select_list 選択リスト
 * @param validations バリデーション
 */
case class FormInsertFormRequestFormCol(
                                         name: String,
                                         col_id: String,
                                         col_index: Int,
                                         col_type: Int,
                                         default_value: String,
                                         select_list: List[FormInsertFormRequestFormColSelect],
                                         validations: FormInsertFormRequestFormColValidation
                                       )

/**
 * フォーム作成API・FormTransferTask・Salesforce・Field
 * @param form_transfer_task_salesforce_id FormTransferTaskSalesforce ID
 * @param form_column_id フォーム項目ID
 * @param field_name Salesforceフィールド名
 */
case class FormInsertFormRequestFormTransferTaskSalesforceField(
                                                                 form_transfer_task_salesforce_id: BigInt,
                                                                 form_column_id: String,
                                                                 field_name: String
                                                               )

/**
 * フォーム作成API・FormTransferTask・Condition
 * @param form_transfer_task_id FormTransferTask ID
 * @param form_id フォームID
 * @param form_col_id フォーム項目ID
 * @param operator 演算子
 * @param cond_value 値　
 */
case class FormInsertFormRequestFormTransferTaskCondition(
                                                           form_transfer_task_id: BigInt,
                                                           form_id: BigInt,
                                                           form_col_id: BigInt,
                                                           operator: String,
                                                           cond_value: String
                                                         )

/**
 * フォーム作成API・FormTransferTask・Mail
 * @param form_transfer_task_id FormTransferTask ID
 * @param from_address_id FROMに使うメールアドレスのID
 * @param to_address Toアドレス
 * @param cc_address Ccアドレス
 * @param bcc_address_id Bccに使うメールアドレスのID
 * @param replyto_address_id replyToに使うメールアドレスのID
 * @param subject 件名
 * @param body 本文
 */
case class FormInsertFormRequestFormTransferTaskMail(
                                                      form_transfer_task_id: BigInt,
                                                      from_address_id: BigInt,
                                                      to_address: String,
                                                      cc_address: String,
                                                      bcc_address_id: BigInt,
                                                      replyto_address_id: BigInt,
                                                      subject: String,
                                                      body: String
                                                    )

/**
 * フォーム作成API・FormTransferTask・Salesforce
 * @param form_transfer_task_id FormTransferTask ID
 * @param object_name Salesforceオブジェクト名
 * @param fields フィールド割り当て情報
 */
case class FormInsertFormRequestFormTransferTaskSalesforce(
                                                            form_transfer_task_id: BigInt,
                                                            object_name: String,
                                                            fields: List[FormInsertFormRequestFormTransferTaskSalesforceField]
                                                          )


/**
 * フォーム作成API・FormTransferTask
 * @param transfer_config_id TransferConfig ID
 * @param form_id Form ID
 * @param task_index 順番
 * @param name 名前
 * @param form_transfer_task_conditions 実行条件データ
 * @param mail MailTransfer設定
 * @param salesforce SalesforceTransfer設定
 */
case class FormInsertFormRequestFormTransferTask(
                                                  transfer_config_id: BigInt,
                                                  form_id: BigInt,
                                                  task_index: Int,
                                                  name: String,
                                                  form_transfer_task_conditions: List[FormInsertFormRequestFormTransferTaskCondition],
                                                  mail: Option[FormInsertFormRequestFormTransferTaskMail],
                                                  salesforce: Option[FormInsertFormRequestFormTransferTaskSalesforce]
                                                )


/**
 * フォーム作成API・フォームデータ
 * @param name フォーム名
 * @param form_index 順番
 * @param title タイトル
 * @param status ステータス
 * @param cancel_url キャンセル時遷移先URL
 * @param close_text フォームクローズ時文言
 * @param complete_url 完了時遷移先URL
 * @param input_header 入力画面のヘッダ文言
 * @param complete_text 完了時の文言
 * @param confirm_header 確認画面のヘッダ文言
 * @param form_cols フォーム項目
 */
case class FormInsertFormRequest(
                                  name: String,
                                  form_index: Int,
                                  title: String,
                                  status: Int,
                                  cancel_url: String,
                                  close_text: String,
                                  complete_url: String,
                                  input_header: String,
                                  complete_text: String,
                                  confirm_header: String,
                                  form_cols: List[FormInsertFormRequestFormCol]
                                ) {

}

trait FormInsertFormRequestJson {
  implicit val FormInsertFormRequestFormColValidationWrites: Writes[FormInsertFormRequestFormColValidation] = (formInsertFormRequestFormColValidation: FormInsertFormRequestFormColValidation) => Json.obj(
    "max_value" -> formInsertFormRequestFormColValidation.max_value,
    "min_value" -> formInsertFormRequestFormColValidation.min_value,
    "max_length" -> formInsertFormRequestFormColValidation.max_length,
    "min_length" -> formInsertFormRequestFormColValidation.min_length,
    "input_type" -> formInsertFormRequestFormColValidation.input_type,
    "required" -> formInsertFormRequestFormColValidation.required
  )

  implicit val FormInsertFormRequestFormColValidationReads: Reads[FormInsertFormRequestFormColValidation] = (
      (JsPath \ "max_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "max_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "input_type").read[Int] ~
      (JsPath \ "required").read[Boolean]
    )(FormInsertFormRequestFormColValidation.apply _)

  implicit val FormInsertFormRequestFormColSelectListWrites: Writes[FormInsertFormRequestFormColSelect] = (formInsertFormRequestFormColSelectList: FormInsertFormRequestFormColSelect) => Json.obj(
    "select_index" -> formInsertFormRequestFormColSelectList.select_index,
    "select_name" -> formInsertFormRequestFormColSelectList.select_name,
    "select_value" -> formInsertFormRequestFormColSelectList.select_value,
    "is_default" -> formInsertFormRequestFormColSelectList.is_default,
    "edit_style" -> formInsertFormRequestFormColSelectList.edit_style,
    "view_style" -> formInsertFormRequestFormColSelectList.view_style
  )

  implicit val FormInsertFormRequestFormColSelectListReads: Reads[FormInsertFormRequestFormColSelect] = (
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormInsertFormRequestFormColSelect.apply _)

  implicit val FormInsertFormRequestFormColWrites: Writes[FormInsertFormRequestFormCol] = (formInsertFormRequestFormCol: FormInsertFormRequestFormCol) => Json.obj(
    "name" -> formInsertFormRequestFormCol.name,
    "col_id" -> formInsertFormRequestFormCol.col_id,
    "col_index" -> formInsertFormRequestFormCol.col_index,
    "col_type" -> formInsertFormRequestFormCol.col_type,
    "default_value" -> formInsertFormRequestFormCol.default_value,
    "select_list" -> formInsertFormRequestFormCol.select_list,
    "validations" -> formInsertFormRequestFormCol.validations
  )

  implicit val FormInsertFormRequestFormColReads: Reads[FormInsertFormRequestFormCol] = (
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormInsertFormRequestFormColSelect]] ~
      (JsPath \ "validations").read[FormInsertFormRequestFormColValidation]
    )(FormInsertFormRequestFormCol.apply _)

  implicit val FormInsertFormRequestWrites: Writes[FormInsertFormRequest] = (formInsertFormRequest: FormInsertFormRequest) => Json.obj(
    "name" -> formInsertFormRequest.name,
    "form_index" -> formInsertFormRequest.form_index,
    "title" -> formInsertFormRequest.title,
    "status" -> formInsertFormRequest.status,
    "cancel_url" -> formInsertFormRequest.cancel_url,
    "close_text" -> formInsertFormRequest.close_text,
    "complete_url" -> formInsertFormRequest.complete_url,
    "input_header" -> formInsertFormRequest.input_header,
    "complete_text" -> formInsertFormRequest.complete_text,
    "confirm_header" -> formInsertFormRequest.confirm_header,
    "form_cols" -> formInsertFormRequest.form_cols
  )

  implicit val FormInsertFormRequestReads: Reads[FormInsertFormRequest] = (
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "cancel_url").read[String] ~
      (JsPath \ "close_text").read[String] ~
      (JsPath \ "complete_url").read[String] ~
      (JsPath \ "input_header").read[String] ~
      (JsPath \ "complete_text").read[String] ~
      (JsPath \ "confirm_header").read[String] ~
      (JsPath \ "form_cols").read[List[FormInsertFormRequestFormCol]]
    )(FormInsertFormRequest.apply _)
}