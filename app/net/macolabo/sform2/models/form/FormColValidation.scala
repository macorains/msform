package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormColValidation(
                              id: BigInt,
                              form_col_id: BigInt,
                              form_id: BigInt,
                              max_value: Option[Int],
                              min_value: Option[Int],
                              max_length: Option[Int],
                              min_length: Option[Int],
                              input_type: Int,
                              required: Boolean,
                              user_group: String,
                              created_user: String,
                              modified_user: String,
                              created: ZonedDateTime,
                              modified: ZonedDateTime
                            ){
  import FormColValidation._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)

}

object FormColValidation extends SQLSyntaxSupport[FormColValidation] {
  override val tableName = "D_FORM_COL_VALIDATION"
  def apply(rs: WrappedResultSet): FormColValidation = {
    FormColValidation(
      rs.bigInt("id"),
      rs.bigInt("form_col_id"),
      rs.bigInt("form_id"),
      rs.intOpt("max_value"),
      rs.intOpt("min_value"),
      rs.intOpt("max_length"),
      rs.intOpt("min_length"),
      rs.int("input_type"),
      rs.boolean("required"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * フォーム項目バリデーションの取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @param session DB Session
   * @return フォーム項目バリデーションのリスト
   */
  def get(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession = autoSession): Option[FormColValidation] = {
    val f = FormColValidation.syntax("f")
    withSQL (
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.max_value,
        f.min_value,
        f.max_length,
        f.min_length,
        f.input_type,
        f.required,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColValidation as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
      ).map(rs => FormColValidation(rs)).single().apply()
  }

  /**
   * フォーム項目バリデーションの作成
   * @param formColValidation フォーム項目バリデーション
   * @param session DB Session
   * @return
   */
  def create(formColValidation: FormColValidation)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormColValidation.column
      insert.into(FormColValidation).namedValues(
        c.form_col_id -> formColValidation.form_col_id,
        c.form_id -> formColValidation.form_id,
        c.max_value -> formColValidation.max_value,
        c.min_value -> formColValidation.min_value,
        c.max_length -> formColValidation.max_length,
        c.min_length -> formColValidation.min_length,
        c.input_type -> formColValidation.input_type,
        c.required -> formColValidation.required,
        c.user_group -> formColValidation.user_group,
        c.created_user -> formColValidation.created_user,
        c.modified_user -> formColValidation.modified_user,
        c.created -> formColValidation.created,
        c.modified -> formColValidation.modified
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  /**
   * フォーム項目バリデーションの更新
   * @param formColValidation フォーム項目バリデーション
   * @param session DB Session
   */
  def save(formColValidation: FormColValidation)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormColValidation.column
      update(FormColValidation).set(
        c.form_col_id -> formColValidation.form_col_id,
        c.form_id -> formColValidation.form_id,
        c.max_value -> formColValidation.max_value,
        c.min_value -> formColValidation.min_value,
        c.max_length -> formColValidation.max_length,
        c.min_length -> formColValidation.min_length,
        c.input_type -> formColValidation.input_type,
        c.required -> formColValidation.required,
        c.modified_user -> formColValidation.modified_user,
        c.modified -> formColValidation.modified
      ).where.eq(c.id, formColValidation.id)
    }.update().apply()
  }

  /**
   * フォーム項目バリデーション削除
   * @param userGroup ユーザーグループ
   * @param formColValidationId フォーム項目バリデーションID
   * @param session DB Session
   * @return
   */
  def erase(userGroup: String, formColValidationId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      delete.from(FormColValidation).where.eq(Form.column.id, formColValidationId).and.eq(Form.column.user_group, userGroup)
    }.update().apply()
  }

}
