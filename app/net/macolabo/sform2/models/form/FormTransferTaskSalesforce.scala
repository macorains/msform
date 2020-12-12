package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskSalesforce(
                                       id: BigInt,
                                       form_transfer_task_id: BigInt,
                                       object_name: String,
                                       user_group: String,
                                       created_user: String,
                                       modified_user: String,
                                       created: ZonedDateTime,
                                       modified: ZonedDateTime
                                     ){
  import FormTransferTaskSalesforce._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object FormTransferTaskSalesforce extends SQLSyntaxSupport[FormTransferTaskSalesforce] {
  override val tableName = "D_FORM_TRANSFER_TASK_SALESFORCE"
  def apply(rs:WrappedResultSet): FormTransferTaskSalesforce = {
    FormTransferTaskSalesforce(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.string("object_name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * FormTransferTaskSalesforce取得
   * @param userGroup ユーザーグループ
   * @param formTransferTaskId TransferTask ID
   * @param session DB Session
   * @return FormTransferTaskSalesforce
   */
  def get(userGroup: String, formTransferTaskId: BigInt)(implicit session: DBSession = autoSession): Option[FormTransferTaskSalesforce] = {
    val f = FormTransferTaskSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.object_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforce as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskSalesforce(rs)).single().apply()
  }

  /**
   * FormTransferTaskSalesforce作成
   * @param formTransferTaskSalesforce FormTransferTaskSalesforce
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(formTransferTaskSalesforce: FormTransferTaskSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      insert.into(FormTransferTaskSalesforce).namedValues(
        c.form_transfer_task_id -> formTransferTaskSalesforce.form_transfer_task_id,
        c.object_name -> formTransferTaskSalesforce.object_name,
        c.user_group -> formTransferTaskSalesforce.user_group,
        c.created_user -> formTransferTaskSalesforce.created_user,
        c.modified_user -> formTransferTaskSalesforce.modified_user,
        c.created -> formTransferTaskSalesforce.created,
        c.modified -> formTransferTaskSalesforce.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * FormTransferTaskSalesforce更新
   * @param formTransferTaskSalesforce FormTransferTaskSalesforce
   * @param session DB Session
   * @return result
   */
  def save(formTransferTaskSalesforce: FormTransferTaskSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      update(FormTransferTaskSalesforce).set(
        c.form_transfer_task_id -> formTransferTaskSalesforce.form_transfer_task_id,
        c.object_name -> formTransferTaskSalesforce.object_name,
        c.user_group -> formTransferTaskSalesforce.user_group,
        c.modified_user -> formTransferTaskSalesforce.modified_user,
        c.modified -> formTransferTaskSalesforce.modified
      ).where.eq(c.id, formTransferTaskSalesforce.id)
    }.update().apply()
  }

  /**
   * FormTransferTaskSalesforce削除
   * @param userGroup ユーザーグループ
   * @param formTransferTaskSalesforceId FormTransferTaskSalesforce ID
   * @param session DB Session
   * @return Result
   */
  def erase(userGroup: String, formTransferTaskSalesforceId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      delete.from(FormTransferTaskSalesforce).where.eq(c.id, formTransferTaskSalesforceId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
}