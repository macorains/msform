package net.macolabo.sform2.models.daos.TransferConfig

import javax.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.{Transfer, TransfersDAO}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.Configuration

class MailTransferConfigDAO @Inject() (
  transfersDao: TransfersDAO,
  configuration: Configuration
) extends BaseTransferConfigDAO {

  override val transferType = 2

  case class MailTransferAddressList(id: Int, name: String, address: String)
  implicit val jsonMailTransferAddressListWrites = (
    (JsPath \ "id").write[Int] ~
    (JsPath \ "name").write[String] ~
    (JsPath \ "address").write[String]
  )(unlift(MailTransferAddressList.unapply))
  implicit val jsonMailTransferAddressListReads = (
    (JsPath \ "id").read[Int] ~
    (JsPath \ "name").read[String] ~
    (JsPath \ "address").read[String]
  )(MailTransferAddressList.apply _)

  case class MailTransferConfig(id: Option[Int], addressList: Option[List[MailTransferAddressList]])
  implicit val jsonMailTransferConfigWrites = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "addressList").writeNullable[List[MailTransferAddressList]]
  )(unlift(MailTransferConfig.unapply))
  implicit val jsonMailTransferConfigReads: Reads[MailTransferConfig] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "addressList").readNullable[List[MailTransferAddressList]]
  )(MailTransferConfig.apply _)

  override def getTransferConfig: JsValue = {
    println("MailTransferInfoDAO")
    transfersDao.getTransfer(transferType) match {
      case t1: List[Transfer] => {
        // 暫定対応
        t1.size match {
          case size1 if size1 > 0 => {
            t1.apply(0).config.validate[MailTransferConfig] match {
              case c1: JsSuccess[MailTransferConfig] => {
                Json.toJson(c1.get)
              }
              case e: JsError => { //Configが取れない時は新規作成
                Json.toJson(MailTransferConfig(None, None))
              }
            }
          }
          case _ => Json.toJson(MailTransferConfig(None, None))
        }

        /*
        t1.config.validate[MailTransferConfig] match {
          case c1: JsSuccess[MailTransferConfig] => {
            Json.toJson(c1.get)
          }
          case e: JsError => { //Configが取れない時は新規作成
            var newConfig = MailTransferConfig(None, None)
            Json.toJson(newConfig)
          }
        }
        */
      }
      case _ => {
        Json.toJson("""{"error" : "2"}""")
      }
    }
  }

  override def saveTransferConfig(config: JsValue, identity: User): JsValue = {
    transfersDao.updateByUserIdentity(identity, transferType, config.toString())
    Json.toJson("""{}""")
  }

}