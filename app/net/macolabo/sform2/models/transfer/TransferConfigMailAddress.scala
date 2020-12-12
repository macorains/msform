package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigMailAddress(
                                    id: BigInt,
                                    transfer_config_mail_id: BigInt,
                                    address_index: Int,
                                    name: String,
                                    address: String,
                                    user_group: String,
                                    created_user: String,
                                    modified_user: String,
                                    created: ZonedDateTime,
                                    modified: ZonedDateTime
                                    ){
  import TransferConfigMailAddress._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object TransferConfigMailAddress extends SQLSyntaxSupport[TransferConfigMailAddress] {
  override val tableName = "D_TRANSFER_CONFIG_MAIL_ADDRESS"
  def apply(rs: WrappedResultSet): TransferConfigMailAddress = {
    TransferConfigMailAddress(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_mail_id"),
      rs.int("address_index"),
      rs.string("name"),
      rs.string("address"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TansferConfigMailAddressのリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @param session DB Session
   * @return TransferCongirMailAddressのリスト
   */
  def getList(userGroup: String, transferConfigMailId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigMailAddress] = {
    val f = TransferConfigMailAddress.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_mail_id,
        f.address_index,
        f.name,
        f.address,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigMailAddress as f)
        .where
        .eq(f.transfer_config_mail_id, transferConfigMailId)
        .and
        .eq(f.user_group, userGroup)
        .orderBy(f.address_index)
    ).map(rs => TransferConfigMailAddress(rs)).list().apply()
  }

  /**
   * TransferCOnfigMailAddress作成
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMailAddress.column
      insert.into(TransferConfigMailAddress).namedValues(
        c.transfer_config_mail_id -> transferConfigMailAddress.transfer_config_mail_id,
        c.address_index -> transferConfigMailAddress.address_index,
        c.name -> transferConfigMailAddress.name,
        c.address -> transferConfigMailAddress.address,
        c.user_group -> transferConfigMailAddress.user_group,
        c.created_user -> transferConfigMailAddress.created_user,
        c.modified_user -> transferConfigMailAddress.modified_user,
        c.created -> transferConfigMailAddress.created,
        c.modified-> transferConfigMailAddress.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigMailAddress更新
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMailAddress.column
      update(TransferConfigMailAddress).set(
        c.transfer_config_mail_id -> transferConfigMailAddress.transfer_config_mail_id,
        c.address_index -> transferConfigMailAddress.address_index,
        c.name -> transferConfigMailAddress.name,
        c.address -> transferConfigMailAddress.address,
        c.user_group -> transferConfigMailAddress.user_group,
        c.created_user -> transferConfigMailAddress.created_user,
        c.modified_user -> transferConfigMailAddress.modified_user,
        c.created -> transferConfigMailAddress.created,
        c.modified-> transferConfigMailAddress.modified
      ).where.eq(c.id, transferConfigMailAddress.id)
    }.update().apply()
  }

  /**
   * TransferConfigMailAddress削除
   * @param userGroup ユーザーグループ
   * @param transferConfigMailAddressId TransferConfigMailAddress ID
   * @param session DB Session
   * @return Result
   */
  def erase(userGroup: String, transferConfigMailAddressId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      delete.from(TransferConfigMailAddress)
        .where
        .eq(TransferConfigMailAddress.column.id, transferConfigMailAddressId)
        .and
        .eq(TransferConfigMailAddress.column.user_group, userGroup)
    }.update().apply()
  }
}