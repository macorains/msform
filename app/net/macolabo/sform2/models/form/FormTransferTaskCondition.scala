package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._
case class FormTransferTaskCondition(
                                    id: Int,
                                    form_transfer_task_id: Int,
                                    form_id: Int,
                                    form_col_id: Int,
                                    operator: String,
                                    cond_value: String,
                                    user_group: String,
                                    created_user: String,
                                    modified_user: String,
                                    created: ZonedDateTime,
                                    modified: ZonedDateTime
                                    )

object FormTransferTaskCondition extends SQLSyntaxSupport[FormTransferTaskCondition] {
  override val tableName = "D_FORM_TRANSFER_TASK_CONDITION"
  def apply(rs: WrappedResultSet): FormTransferTaskCondition = {
    FormTransferTaskCondition(
      rs.int("id"),
      rs.int("form_transfer_task_id"),
      rs.int("form_id"),
      rs.int("form_col_id"),
      rs.string("operator"),
      rs.string("cond_value"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * FormTransferTaskConditonの取得
   * @param formTransferTaskConditionId FormTransferTaskCondition ID
   * @param session DB Session
   * @return FormTransferTaskCondition
   */
  def get(formTransferTaskConditionId: Int)(implicit session: DBSession = autoSession): Option[FormTransferTaskCondition] = {
    val f = FormTransferTaskCondition.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskCondition as f)
        .where
        .eq(f.id, formTransferTaskConditionId)
    ).map(rs => FormTransferTaskCondition(rs)).single().apply()
  }

  /**
   * FormTransferTaskConditionのリスト取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formTransferTaskId FormTransferTask ID
   * @param session DB Session
   * @return FormTransferTaskConditionのリスト
   */
  def getList(userGroup: String, formId: Int, formTransferTaskId: Int)(implicit session: DBSession = autoSession): List[FormTransferTaskCondition] = {
    val f = FormTransferTaskCondition.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskCondition as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_transfer_task_id, formTransferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormTransferTaskCondition(rs)).list().apply()
  }

  /**
   * FormTransferTaskConditionの作成
   * @param formTransferTaskCondition FormTransferTaskCondition
   * @param session DB Session
   * @return
   */
  def create(formTransferTaskCondition: FormTransferTaskCondition)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = FormTransferTaskCondition.column
      insert
        .into(FormTransferTaskCondition)
        .namedValues(
          c.form_transfer_task_id -> formTransferTaskCondition.form_transfer_task_id,
          c.form_id -> formTransferTaskCondition.form_id,
          c.form_col_id -> formTransferTaskCondition.form_col_id,
          c.operator -> formTransferTaskCondition.operator,
          c.cond_value -> formTransferTaskCondition.cond_value,
          c.user_group -> formTransferTaskCondition.user_group,
          c.created_user -> formTransferTaskCondition.created_user,
          c.modified_user -> formTransferTaskCondition.modified_user,
          c.created -> formTransferTaskCondition.created,
          c.modified -> formTransferTaskCondition.modified
        )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * FormTransferTaskConditionの更新
   * @param formTransferTaskCondition FormTransferTaskCondition
   * @param session DB Session
   * @return
   */
  def save(formTransferTaskCondition: FormTransferTaskCondition)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = FormTransferTaskCondition.column
      update(FormTransferTaskCondition)
        .set(
          c.form_transfer_task_id -> formTransferTaskCondition.form_transfer_task_id,
          c.form_id -> formTransferTaskCondition.form_id,
          c.form_col_id -> formTransferTaskCondition.form_col_id,
          c.operator -> formTransferTaskCondition.operator,
          c.cond_value -> formTransferTaskCondition.cond_value,
          c.user_group -> formTransferTaskCondition.user_group,
          c.created_user -> formTransferTaskCondition.created_user,
          c.modified_user -> formTransferTaskCondition.modified_user,
          c.created -> formTransferTaskCondition.created,
          c.modified -> formTransferTaskCondition.modified
        )
    }.update().apply()
  }
}

