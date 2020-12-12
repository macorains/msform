package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigSalesforceObject(
                                         id: BigInt,
                                         transfer_config_salesforce_id: BigInt,
                                         name: String,
                                         label: String,
                                         active: Boolean,
                                         user_group: String,
                                         created_user: String,
                                         modified_user: String,
                                         created: ZonedDateTime,
                                         modified: ZonedDateTime
                                         ) {
  import TransferConfigSalesforceObject._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object TransferConfigSalesforceObject extends SQLSyntaxSupport[TransferConfigSalesforceObject] {
  override val tableName = "D_TRANSFER_CONFIG_SALESFORCE_OBJECT"
  def apply(rs:WrappedResultSet): TransferConfigSalesforceObject = {
    TransferConfigSalesforceObject(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_salesforce_id"),
      rs.string("name"),
      rs.string("label"),
      rs.boolean("active"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TransferConfigSalesforceObjectのリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @param session DB Session
   * @return TransferConfigSalesforceObjectのリスト
   */
  def getList(userGroup: String, transferConfigSalesforceId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigSalesforceObject] = {
    val f = TransferConfigSalesforceObject.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_salesforce_id,
        f.name,
        f.label,
        f.active,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigSalesforceObject as f)
        .where
        .eq(f.transfer_config_salesforce_id, transferConfigSalesforceId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigSalesforceObject(rs)).list().apply()
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param transferConfigSalesforceObject　TransferConfigSalesforceObject
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      insert.into(TransferConfigSalesforceObject).namedValues(
        c.transfer_config_salesforce_id -> transferConfigSalesforceObject.transfer_config_salesforce_id,
        c.name -> transferConfigSalesforceObject.name,
        c.label -> transferConfigSalesforceObject.label,
        c.active -> transferConfigSalesforceObject.active,
        c.user_group -> transferConfigSalesforceObject.user_group,
        c.created_user -> transferConfigSalesforceObject.created_user,
        c.modified_user -> transferConfigSalesforceObject.modified_user,
        c.created -> transferConfigSalesforceObject.created,
        c.modified -> transferConfigSalesforceObject.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigSalesforceObject保存
   * @param transferConfigSalesforceObject　TransferConfigSalesforceObject
   * @param session DB Session
   * @return result
   */
  def save(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      update(TransferConfigSalesforceObject).set(
        c.transfer_config_salesforce_id -> transferConfigSalesforceObject.transfer_config_salesforce_id,
        c.name -> transferConfigSalesforceObject.name,
        c.label -> transferConfigSalesforceObject.label,
        c.active -> transferConfigSalesforceObject.active,
        c.user_group -> transferConfigSalesforceObject.user_group,
        c.modified_user -> transferConfigSalesforceObject.modified_user,
        c.modified -> transferConfigSalesforceObject.modified
      ).where.eq(c.id, transferConfigSalesforceObject.id)
    }.update().apply()
  }

  /**
   * TransferConfigSalesforceObject削除
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param session DB Session
   * @return Result
   */
  def erase(userGroup: String, transferConfigSalesforceObjectId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSalesforceObject.column
      delete.from(TransferConfigSalesforceObject).where.eq(c.id, transferConfigSalesforceObjectId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
}


