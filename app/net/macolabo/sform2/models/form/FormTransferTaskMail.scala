package net.macolabo.sform2.models.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskMail(
                               id: BigInt,
                               form_transfer_task_id: BigInt,
                               from_address_id: BigInt,
                               to_address: String,
                               cc_address: String,
                               bcc_address_id: BigInt,
                               replyto_address_id: BigInt,
                               subject: String,
                               body: String,
                               user_group: String,
                               created_user: String,
                               modified_user: String,
                               created: ZonedDateTime,
                               modified: ZonedDateTime
                               ){
  import FormTransferTaskMail._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object FormTransferTaskMail extends SQLSyntaxSupport[FormTransferTaskMail] {
  override val tableName = "D_FORM_TRANSFER_TASK_MAIL"
  def apply(rs:WrappedResultSet): FormTransferTaskMail = {
    FormTransferTaskMail(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.bigInt("from_address_id"),
      rs.string("to_address"),
      rs.string("cc_address"),
      rs.bigInt("bcc_address_id"),
      rs.bigInt("replyto_address_id"),
      rs.string("subject"),
      rs.string("body"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * FormTransferTaskMail取得
   * @param userGroup ユーザーグループ
   * @param formTransferTaskId TransferTask ID
   * @param session DB Session
   * @return FormTransferTaskMail
   */
  def get(userGroup: String, formTransferTaskId: BigInt)(implicit session: DBSession = autoSession): Option[FormTransferTaskMail] = {
    val f = FormTransferTaskMail.syntax("f")
    withSQL(
    select(
      f.id,
      f.form_transfer_task_id,
      f.from_address_id,
      f.to_address,
      f.cc_address,
      f.bcc_address_id,
      f.replyto_address_id,
      f.subject,
      f.body,
      f.user_group,
      f.created_user,
      f.modified_user,
      f.created,
      f.modified
    )
      .from(FormTransferTaskMail as f)
      .where
      .eq(f.form_transfer_task_id, formTransferTaskId)
      .and
      .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskMail(rs)).single().apply()
  }

  /**
   * FormTransferTaskMail作成
   * @param formTransferTaskMail FormTransferTaskMail
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(formTransferTaskMail: FormTransferTaskMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskMail.column
      insert.into(FormTransferTaskMail).namedValues(
        c.form_transfer_task_id -> formTransferTaskMail.form_transfer_task_id,
        c.from_address_id -> formTransferTaskMail.from_address_id,
        c.to_address -> formTransferTaskMail.to_address,
        c.cc_address -> formTransferTaskMail.cc_address,
        c.bcc_address_id -> formTransferTaskMail.bcc_address_id,
        c.replyto_address_id -> formTransferTaskMail.replyto_address_id,
        c.subject -> formTransferTaskMail.subject,
        c.body -> formTransferTaskMail.body,
        c.user_group -> formTransferTaskMail.user_group,
        c.created_user -> formTransferTaskMail.created_user,
        c.modified_user -> formTransferTaskMail.modified_user,
        c.created -> formTransferTaskMail.created,
        c.modified -> formTransferTaskMail.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * FormTransferTaskMail更新
   * @param formTransferTaskMail FormTransferTaskMail
   * @param session DB Session
   * @return result
   */
  def save(formTransferTaskMail: FormTransferTaskMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskMail.column
      update(FormTransferTaskMail).set(
        c.form_transfer_task_id -> formTransferTaskMail.form_transfer_task_id,
        c.from_address_id -> formTransferTaskMail.from_address_id,
        c.to_address -> formTransferTaskMail.to_address,
        c.cc_address -> formTransferTaskMail.cc_address,
        c.bcc_address_id -> formTransferTaskMail.bcc_address_id,
        c.replyto_address_id -> formTransferTaskMail.replyto_address_id,
        c.subject -> formTransferTaskMail.subject,
        c.body -> formTransferTaskMail.body,
        c.user_group -> formTransferTaskMail.user_group,
        c.created_user -> formTransferTaskMail.created_user,
        c.modified_user -> formTransferTaskMail.modified_user,
        c.created -> formTransferTaskMail.created,
        c.modified -> formTransferTaskMail.modified
      ).where.eq(c.id, formTransferTaskMail.id)
    }.update().apply()
  }

  /**
   * FormTransferTaskMailの削除
   * @param userGroup ユーザーグループ
   * @param formTransferTaskMailId FormTransferTaskMail ID
   * @param session DB Session
   * @return Result
   */
  def erase(userGroup: String, formTransferTaskMailId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = FormTransferTaskMail.column
      delete.from(FormTransferTaskMail).where.eq(c.id, formTransferTaskMailId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
}