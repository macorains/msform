package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigSalesforce(
                                   id: BigInt,
                                   transfer_config_id: BigInt,
                                   sf_user_name: String,
                                   sf_password: String,
                                   sf_security_token: String,
                                   user_group: String,
                                   created_user: String,
                                   modified_user: String,
                                   created: ZonedDateTime,
                                   modified: ZonedDateTime
                                   ){
  import TransferConfigSalesforce._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}


object TransferConfigSalesforce extends SQLSyntaxSupport[TransferConfigSalesforce] {
  override val tableName = "D_TRANSFER_CONFIG_SALESFORCE"
  def apply(rs:WrappedResultSet): TransferConfigSalesforce = {
    TransferConfigSalesforce(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.string("sf_user_name"),
      rs.string("sf_password"),
      rs.string("sf_security_token"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TransferConfigSalesforce取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfigSalesforce
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSalesforce] = {
    val f = TransferConfigSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.sf_user_name,
        f.sf_password,
        f.sf_security_token,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigSalesforce as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigSalesforce(rs)).single().apply()
  }

  /**
   * TransferConfigSalesforce作成
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfigSalesforce.column
      insert.into(TransferConfigSalesforce).namedValues(
        c.transfer_config_id -> transferConfigSalesforce.transfer_config_id,
        c.sf_user_name -> transferConfigSalesforce.sf_user_name,
        c.sf_password -> transferConfigSalesforce.sf_password,
        c.sf_security_token -> transferConfigSalesforce.sf_security_token,
        c.user_group -> transferConfigSalesforce.user_group,
        c.created_user -> transferConfigSalesforce.created_user,
        c.modified_user -> transferConfigSalesforce.modified_user,
        c.created -> transferConfigSalesforce.created,
        c.modified -> transferConfigSalesforce.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigSalesforce更新
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfigSalesforce.column
      update(TransferConfigSalesforce).set(
        c.transfer_config_id -> transferConfigSalesforce.transfer_config_id,
        c.sf_user_name -> transferConfigSalesforce.sf_user_name,
        c.sf_password -> transferConfigSalesforce.sf_password,
        c.sf_security_token -> transferConfigSalesforce.sf_security_token,
        c.user_group -> transferConfigSalesforce.user_group,
        c.created_user -> transferConfigSalesforce.created_user,
        c.modified_user -> transferConfigSalesforce.modified_user,
        c.created -> transferConfigSalesforce.created,
        c.modified -> transferConfigSalesforce.modified
      ).where.eq(c.id, transferConfigSalesforce.id)
    }.update().apply()
  }
}
