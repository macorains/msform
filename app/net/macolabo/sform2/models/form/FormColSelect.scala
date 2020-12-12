package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * フォーム項目 選択肢
 * @param id フォーム項目 選択肢ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param select_index 選択項目番号
 * @param select_name 選択項目名
 * @param select_value 選択項目の値
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時cssスタイル
 * @param view_style 表示時cssスタイル
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class FormColSelect(
                          id: BigInt,
                          form_col_id: BigInt,
                          form_id: BigInt,
                          select_index: Int,
                          select_name: String,
                          select_value: String,
                          is_default: Boolean,
                          edit_style: Option[String],
                          view_style: Option[String],
                          user_group: String,
                          created_user: String,
                          modified_user: String,
                          created: ZonedDateTime,
                          modified: ZonedDateTime
                        ){
  import FormColSelect._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)

}

object FormColSelect extends SQLSyntaxSupport[FormColSelect] {
  override val tableName = "D_FORM_COL_SELECT"
  def apply(rs: WrappedResultSet) :FormColSelect = {
    FormColSelect(
      rs.bigInt("id"),
      rs.bigInt("form_col_id"),
      rs.bigInt("form_id"),
      rs.int("select_index"),
      rs.string("select_name"),
      rs.string("select_value"),
      rs.boolean("is_default"),
      rs.stringOpt("edit_style"),
      rs.stringOpt("view_style"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * フォーム項目選択項目リスト取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @param session DB Session
   * @return フォーム項目選択項目のリスト
   */
  def getList(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession = autoSession): List[FormColSelect] = {
    val f = FormColSelect.syntax("f")
    withSQL (
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.select_index,
        f.select_name,
        f.select_value,
        f.is_default,
        f.edit_style,
        f.view_style,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColSelect as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
      ).map(rs => FormColSelect(rs)).list().apply()
  }

  /**
   * フォーム項目選択項目の作成
   * @param formColSelect フォーム項目選択項目
   * @param session DB Session
   * @return フォーム項目選択項目のID
   */
  def create(formColSelect: FormColSelect)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = FormColSelect.column
      insert.into(FormColSelect).namedValues(
        c.form_col_id -> formColSelect.form_col_id,
        c.form_id -> formColSelect.form_id,
        c.select_index -> formColSelect.select_index,
        c.select_name -> formColSelect.select_name,
        c.select_value -> formColSelect.select_value,
        c.is_default -> formColSelect.is_default,
        c.edit_style -> formColSelect.edit_style,
        c.view_style -> formColSelect.view_style,
        c.user_group -> formColSelect.user_group,
        c.created_user -> formColSelect.created_user,
        c.modified_user -> formColSelect.modified_user,
        c.created -> formColSelect.created,
        c.modified -> formColSelect.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * フォーム項目選択項目の更新
   * @param formColSelect フォーム項目選択項目
   * @param session DB Session
   * @return
   */
  def save(formColSelect: FormColSelect)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormColSelect.column
      update(FormColSelect).set(
        c.form_col_id -> formColSelect.form_col_id,
        c.form_id -> formColSelect.form_id,
        c.select_index -> formColSelect.select_index,
        c.select_name -> formColSelect.select_name,
        c.select_value -> formColSelect.select_value,
        c.is_default -> formColSelect.is_default,
        c.edit_style -> formColSelect.edit_style,
        c.view_style -> formColSelect.view_style,
        c.modified_user -> formColSelect.modified_user,
        c.modified -> formColSelect.modified
      ).where.eq(c.id, formColSelect.id)
    }.update().apply()
  }

  /**
   * フォーム項目削除
   * @param userGroup ユーザーグループ
   * @param formColSelectId フォーム項目選択項目ID
   * @param session DB Session
   * @return
   */
  def erase(userGroup: String, formColSelectId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      delete.from(FormColSelect).where.eq(Form.column.id, formColSelectId).and.eq(Form.column.user_group, userGroup)
    }.update().apply()
  }

}