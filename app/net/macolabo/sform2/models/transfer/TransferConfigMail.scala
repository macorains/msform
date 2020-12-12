package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigMail(
                             id: BigInt,
                             transfer_config_id: BigInt,
                             use_cc: Boolean,
                             use_bcc: Boolean,
                             use_replyto: Boolean,
                             user_group: String,
                             created_user: String,
                             modified_user: String,
                             created: ZonedDateTime,
                             modified: ZonedDateTime
                             ){
  import TransferConfigMail._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object TransferConfigMail extends SQLSyntaxSupport[TransferConfigMail] {
  override val tableName = "D_TRANSFER_CONFIG_MAIL"
  def apply(rs:WrappedResultSet): TransferConfigMail = {
    TransferConfigMail(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.boolean("use_cc"),
      rs.boolean("use_bcc"),
      rs.boolean("use_replyto"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TransferConfigMail取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfigMail
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigMail] = {
    val f = TransferConfigMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.use_cc,
        f.use_bcc,
        f.use_replyto,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigMail(rs)).single().apply()
  }

  /**
   * TransferConfigMail作成
   * @param transferConfigMail TransferConfigMail
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMail:TransferConfigMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMail.column
      insert.into(TransferConfigMail).namedValues(
        c.transfer_config_id -> transferConfigMail.transfer_config_id,
        c.use_cc -> transferConfigMail.use_cc,
        c.use_bcc -> transferConfigMail.use_bcc,
        c.use_replyto -> transferConfigMail.use_replyto,
        c.user_group -> transferConfigMail.user_group,
        c.created_user -> transferConfigMail.created_user,
        c.modified_user -> transferConfigMail.modified_user,
        c.created -> transferConfigMail.created,
        c.modified -> transferConfigMail.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigMail更新
   * @param transferConfigMail TransferConfigMail
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigMail:TransferConfigMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMail.column
      update(TransferConfigMail).set(
        c.transfer_config_id -> transferConfigMail.transfer_config_id,
        c.use_cc -> transferConfigMail.use_cc,
        c.use_bcc -> transferConfigMail.use_bcc,
        c.use_replyto -> transferConfigMail.use_replyto,
        c.user_group -> transferConfigMail.user_group,
        c.created_user -> transferConfigMail.created_user,
        c.modified_user -> transferConfigMail.modified_user,
        c.created -> transferConfigMail.created,
        c.modified -> transferConfigMail.modified
      ).where.eq(c.id, transferConfigMail.id)
    }.update().apply()
  }
}
