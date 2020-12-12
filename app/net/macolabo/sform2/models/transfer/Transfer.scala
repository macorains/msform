package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * Transfer
 * @param id Transfer ID
 * @param type_code Transfer Type Code
 * @param name Transferの名前
 * @param status ステータス
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class Transfer(
                     id: BigInt,
                     type_code: String,
                     name: String,
                     status: Int,
                     created_user: String,
                     modified_user: String,
                     created: ZonedDateTime,
                     modified: ZonedDateTime
                   )

object Transfer extends SQLSyntaxSupport[Transfer] {
  override val tableName = "M_TRANSFER"
  def apply(rs: WrappedResultSet):Transfer = {
    Transfer(
      rs.bigInt("id"),
      rs.string("type_code"),
      rs.string("name"),
      rs.int("status"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * Transfer取得
   * @param transferId Transfer ID
   * @param session DB Sessoin
   * @return Transfer
   */
  def get(transferId: BigInt)(implicit session: DBSession = autoSession) :Option[Transfer] = {
    val f = Transfer.syntax("f")
    withSQL (
      select(
        f.id,
        f.type_code,
        f.name,
        f.status,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(Transfer as f)
        .where
        .eq(f.id, transferId)
    ).map(rs => Transfer(rs)).single().apply()
  }

  /**
   * Transferのリスト取得
   * @param session DB Session
   * @return Transferのリスト
   */
  def getList()(implicit session: DBSession = autoSession): List[Transfer] = {
    val f = Transfer.syntax("f")
    withSQL (
      select(
        f.id,
        f.type_code,
        f.name,
        f.status,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(Transfer as f)
    ).map(rs => Transfer(rs)).list().apply()
  }
}