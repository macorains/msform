package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskSalesforceField(
                                            id: BigInt,
                                            form_transfer_task_salesforce_id: BigInt,
                                            form_column_id: String,
                                            field_name: String,
                                            user_group: String,
                                            created_user: String,
                                            modified_user: String,
                                            created: ZonedDateTime,
                                            modified: ZonedDateTime
                                          ){
  import FormTransferTaskSalesforceField._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object FormTransferTaskSalesforceField extends SQLSyntaxSupport[FormTransferTaskSalesforceField] {
  override val tableName = "D_FORM_TRANSFER_TASK_SALESFORCE_FIELD"
  def apply(rs:WrappedResultSet): FormTransferTaskSalesforceField = {
    FormTransferTaskSalesforceField(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_salesforce_id"),
      rs.string("form_column_id"),
      rs.string("field_name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * FormTransferTaskSalesforceFieldのリスト取得
   * @param userGroup ユーザーグループ
   * @param formTransferTaskSalesforceId　TransferTaskSalesforce ID
   * @param session DB Session
   * @return FormTransferTaskSalesforceFieldのリスト
   */
  def getList(userGroup: String, formTransferTaskSalesforceId: BigInt)(implicit session: DBSession = autoSession): List[FormTransferTaskSalesforceField] = {
    val f = FormTransferTaskSalesforceField.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_salesforce_id,
        f.form_column_id,
        f.field_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforceField as f)
        .where
        .eq(f.form_transfer_task_salesforce_id, formTransferTaskSalesforceId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskSalesforceField(rs)).list().apply()
  }

  /**
   * FormTransferTaskSalesforceField作成
   * @param formTransferTaskSalesforceField FormTransferTaskSalesforceField
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(formTransferTaskSalesforceField: FormTransferTaskSalesforceField)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = FormTransferTaskSalesforceField.column
      insert.into(FormTransferTaskSalesforceField).namedValues(
        c.form_transfer_task_salesforce_id -> formTransferTaskSalesforceField.form_transfer_task_salesforce_id,
        c.form_column_id -> formTransferTaskSalesforceField.form_column_id,
        c.field_name -> formTransferTaskSalesforceField.field_name,
        c.user_group -> formTransferTaskSalesforceField.user_group,
        c.created_user -> formTransferTaskSalesforceField.created_user,
        c.modified_user -> formTransferTaskSalesforceField.modified_user,
        c.created -> formTransferTaskSalesforceField.created,
        c.modified -> formTransferTaskSalesforceField.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * FormTransferTaskSalesforceField更新
   * @param formTransferTaskSalesforceField FormTransferTaskSalesforceField
   * @param session DB Session
   * @return Result
   */
  def save(formTransferTaskSalesforceField: FormTransferTaskSalesforceField)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = FormTransferTaskSalesforceField.column
      update(FormTransferTaskSalesforceField).set(
        c.form_transfer_task_salesforce_id -> formTransferTaskSalesforceField.form_transfer_task_salesforce_id,
        c.form_column_id -> formTransferTaskSalesforceField.form_column_id,
        c.field_name -> formTransferTaskSalesforceField.field_name,
        c.user_group -> formTransferTaskSalesforceField.user_group,
        c.modified_user -> formTransferTaskSalesforceField.modified_user,
        c.modified -> formTransferTaskSalesforceField.modified
      ).where.eq(c.id, formTransferTaskSalesforceField.id)
    }.update().apply()
  }

  /**
   * FormTransferTaskSalesforceField削除
   * @param userGroup ユーザーグループ
   * @param formTransferTaskSalesforceFieldId FormTransferTaskSalesforceField ID
   * @param session DB Session
   * @return Result
   */
  def erase(userGroup: String, formTransferTaskSalesforceFieldId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforceField.column
      delete.from(FormTransferTaskSalesforceField).where.eq(c.id, formTransferTaskSalesforceFieldId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
}
