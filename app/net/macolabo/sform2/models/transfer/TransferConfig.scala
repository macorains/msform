package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * Transfer設定
 * @param id Transfer設定ID
 * @param type_code Transfer Type Code
 * @param config_index Transfer設定の番号
 * @param name 名前
 * @param status ステータス
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class TransferConfig(
                           id: BigInt,
                           type_code: String,
                           config_index: Int,
                           name: String,
                           status: Int,
                           user_group: String,
                           created_user: String,
                           modified_user: String,
                           created: ZonedDateTime,
                           modified: ZonedDateTime
                         ){
  import TransferConfig._
  def insert: BigInt = create(this)
  def update: BigInt = save(this)
}

object TransferConfig extends SQLSyntaxSupport[TransferConfig] {
  override val tableName = "D_TRANSFER_CONFIG"
  def apply(rs: WrappedResultSet): TransferConfig = {
    TransferConfig(
      rs.bigInt("id"),
      rs.string("type_code"),
      rs.int("config_index"),
      rs.string("name"),
      rs.int("status"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * TransferConfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfig
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfig] = {
    val f = TransferConfig.syntax("f")
    withSQL(
      select(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfig as f)
        .where
        .eq(f.id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfig(rs)).single().apply()
  }

  /**
   * TransferConfig リスト取得
   * @param userGroup ユーザーグループ
   * @param session DB Session
   * @return TransferConfigのリスト
   */
  def getList(userGroup: String)(implicit session: DBSession = autoSession): List[TransferConfig] = {
    val f = TransferConfig.syntax("f")
    withSQL(
      select(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfig as f)
        .where
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfig(rs)).list().apply()
  }

  /**
   * TransferConfig作成
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfig.column
      insert.into(TransferConfig).namedValues(
        c.type_code -> transferConfig.type_code,
        c.config_index -> transferConfig.config_index,
        c.name -> transferConfig.name,
        c.status -> transferConfig.status,
        c.user_group -> transferConfig.user_group,
        c.created_user -> transferConfig.created_user,
        c.modified_user -> transferConfig.modified_user,
        c.created -> transferConfig.created,
        c.modified -> transferConfig.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfig更新
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return Result
   */
  def save(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfig.column
      update(TransferConfig).set(
        c.type_code -> transferConfig.type_code,
        c.config_index -> transferConfig.config_index,
        c.name -> transferConfig.name,
        c.status -> transferConfig.status,
        c.user_group -> transferConfig.user_group,
        c.modified_user -> transferConfig.modified_user,
        c.modified -> transferConfig.modified
      ).where.eq(c.id, transferConfig.id)
    }.update().apply()
  }
}
