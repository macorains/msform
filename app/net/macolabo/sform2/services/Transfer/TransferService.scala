package net.macolabo.sform2.services.Transfer

import java.time.ZonedDateTime

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce, TransferConfigSalesforceObject, TransferConfigSalesforceObjectField}

import scala.concurrent.ExecutionContext

class TransferService @Inject() (implicit ex: ExecutionContext) extends TransferGetTransferConfigSelectListJson with TransferGetTransferConfigListJson with TransferGetTransferConfigResponseJson {

  /**
   * フォーム編集画面のTransferConfig選択リスト用データ取得
   * @param identity 認証情報
   * @return TransferConfigのID,Nameのリスト
   */
  def getTransferConfigSelectList(identity: User): List[TransferGetTransferConfigSelectList] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigSelectList(
        f.id,
        f.name,
        f.type_code
      )
    })
  }

  /**
   * TransferConfigのリスト取得
   * @param identity 認証情報
   * @return TransferConfigのリスト
   */
  def getTransferConfigList(identity: User): List[TransferGetTransferConfigListResponse] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigListResponse(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status
      )
    })
  }

  /**
   * TransferConfigの詳細付きデータ取得
   * @param identity 認証情報
   * @param transferConfigId TransferConfig ID
   * @return 詳細付きTransferConfig
   */
  def getTransferConfig(identity:User, transferConfigId: Int): Option[TransferGetTransferConfigResponse] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferConfigResponse(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        TransferGetTransferResponseConfigDetail(
          getTransferConfigMail(userGroup, transferConfigId),
          getTransferConfigSalesforce(userGroup, transferConfigId)
        )
      )
    })
  }

  /**
   * TransferConfigの更新
   * @param identity 認証情報
   * @param transferUpdateTransferConfigRequest TransferConfig更新リクエスト
   * @return Result
   */
  def updateTransferConfig(identity: User, transferUpdateTransferConfigRequest: TransferUpdateTransferConfigRequest): TransferUpdateTransferConfigResponse = {
    val userId = identity.userID.toString
    val userGroup = identity.group.getOrElse("")
    TransferConfig(
      transferUpdateTransferConfigRequest.id,
      transferUpdateTransferConfigRequest.type_code,
      transferUpdateTransferConfigRequest.config_index,
      transferUpdateTransferConfigRequest.name,
      transferUpdateTransferConfigRequest.status,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update

    transferUpdateTransferConfigRequest.detail.mail.map(d => {
      updateTransferConfigMail(userGroup, userId, d)
    })
    transferUpdateTransferConfigRequest.detail.salesforce.map(d => {
      updateTransferConfigSalesforce(userGroup, userId, d)
    })
    TransferUpdateTransferConfigResponse(transferUpdateTransferConfigRequest.id)
  }

  /**
   * TransferConfigMail更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfig TransferConfigMail更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMail(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfig: TransferUpdateTransferRequestMailTransferConfig): BigInt = {
    TransferConfigMail(
      transferUpdateTransferRequestMailTransferConfig.id,
      transferUpdateTransferRequestMailTransferConfig.transfer_config_id,
      transferUpdateTransferRequestMailTransferConfig.use_cc,
      transferUpdateTransferRequestMailTransferConfig.use_bcc,
      transferUpdateTransferRequestMailTransferConfig.use_replyto,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update
    val updateMailAddressList = transferUpdateTransferRequestMailTransferConfig.mail_address_list.map(m => {
      m.id match {
        case Some(s: BigInt) if s > 0 => updateTransferConfigMailAddress(userGroup, userId, m)
        case _ => insertTransferConfigMailAddress(userGroup, userId, m)
      }
    })
    TransferConfigMailAddress.getList(userGroup, transferUpdateTransferRequestMailTransferConfig.id)
        .filterNot(c => updateMailAddressList.contains(c.id))
        .map(c => c.id)
        .foreach(c => TransferConfigMailAddress.erase(userGroup, c))
    transferUpdateTransferRequestMailTransferConfig.id
  }

  /**
   * TransferConfigMailAddress更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress): BigInt = {
    val id = transferUpdateTransferRequestMailTransferConfigMailAddress.id.getOrElse(BigInt(0))
    TransferConfigMailAddress(
      id,
      transferUpdateTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
      transferUpdateTransferRequestMailTransferConfigMailAddress.address_index,
      transferUpdateTransferRequestMailTransferConfigMailAddress.name,
      transferUpdateTransferRequestMailTransferConfigMailAddress.address,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update
    id
  }

  /**
   * TransferConfigMailAddress作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return
   */
  private def insertTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress): BigInt = {
    TransferConfigMailAddress(
      transferUpdateTransferRequestMailTransferConfigMailAddress.id.getOrElse(0),
      transferUpdateTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
      transferUpdateTransferRequestMailTransferConfigMailAddress.address_index,
      transferUpdateTransferRequestMailTransferConfigMailAddress.name,
      transferUpdateTransferRequestMailTransferConfigMailAddress.address,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).insert
  }

  /**
   * TransferConfigSalesforce更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfig TransferConfigSalesforce更新リクエスト
   * @return
   */
  private def updateTransferConfigSalesforce(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfig: TransferUpdateTransferRequestSalesforceTransferConfig): BigInt = {
    TransferConfigSalesforce(
      transferUpdateTransferRequestSalesforceTransferConfig.id,
      transferUpdateTransferRequestSalesforceTransferConfig.transfer_config_id,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_user_name,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_password,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_security_token,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update

    val updatedObjects = transferUpdateTransferRequestSalesforceTransferConfig.objects.map(o => {
      o.id match {
        case Some(i: BigInt) if i>0 => updateTransferConfigSalesforceObject(userGroup, userId, o)
        case _ => insertTransferConfigSalesforceObject(userGroup, userId, transferUpdateTransferRequestSalesforceTransferConfig.id, TransferUpdateTransferRequestSalesforceTransferConfigObjectToTransferInsertTransferRequestSalesforceTransferConfigObject(o))
      }
    })

    TransferConfigSalesforceObject
      .getList(userGroup, transferUpdateTransferRequestSalesforceTransferConfig.id)
      .filterNot(o => updatedObjects.contains(o.id))
      .map(o => o.id)
      .foreach(o => TransferConfigSalesforceObject.erase(userGroup, o))

    transferUpdateTransferRequestSalesforceTransferConfig.id
  }

  /**
   * TransferConfigSalesforceObject更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfigObject　TransferUpdateTransferRequestSalesforceTransferConfigObject
   * @return 更新したID
   */
  private def updateTransferConfigSalesforceObject(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfigObject: TransferUpdateTransferRequestSalesforceTransferConfigObject): BigInt = {
    TransferConfigSalesforceObject(
      transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)),
      transferUpdateTransferRequestSalesforceTransferConfigObject.transfer_config_salesforce_id,
      transferUpdateTransferRequestSalesforceTransferConfigObject.name,
      transferUpdateTransferRequestSalesforceTransferConfigObject.label,
      transferUpdateTransferRequestSalesforceTransferConfigObject.active,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update

    val updatedFields = transferUpdateTransferRequestSalesforceTransferConfigObject.fields.map(f => {
      f.id match {
        case Some(i: BigInt) if i>0 => updateTransferConfigSalesforceObjectField(userGroup, userId, f)
        case _ => insertTransferConfigSalesforceObjectField(userGroup, userId, transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)), TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(f))
      }
    })

    TransferConfigSalesforceObjectField
      .getList(userGroup, transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)))
        .filterNot(f => updatedFields.contains(f.id))
        .map(f => f.id)
        .foreach(f => TransferConfigSalesforceObject.erase(userGroup, f))

    transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0))
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @param transferInsertTransferRequestSalesforceTransferConfigObject TransferInsertTransferRequestSalesforceTransferConfigObject
   * @return 作成したレコードのID
   */
  private def insertTransferConfigSalesforceObject(userGroup: String, userId: String, transferConfigSalesforceId: BigInt, transferInsertTransferRequestSalesforceTransferConfigObject: TransferInsertTransferRequestSalesforceTransferConfigObject): BigInt = {
    TransferConfigSalesforceObject(
      0,
      transferConfigSalesforceId,
      transferInsertTransferRequestSalesforceTransferConfigObject.name,
      transferInsertTransferRequestSalesforceTransferConfigObject.label,
      transferInsertTransferRequestSalesforceTransferConfigObject.active,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).insert
  }

  /**
   * TransferConfigSalesforceObject更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfigObjectField TransferUpdateTransferRequestSalesforceTransferConfigObjectField
   * @return 更新したID
   */
  private def updateTransferConfigSalesforceObjectField(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfigObjectField: TransferUpdateTransferRequestSalesforceTransferConfigObjectField): BigInt = {
    TransferConfigSalesforceObjectField(
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.id.getOrElse(0),
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.transfer_config_salesforce_object_id,
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.name,
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.label,
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.field_type,
      transferUpdateTransferRequestSalesforceTransferConfigObjectField.active,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update
    transferUpdateTransferRequestSalesforceTransferConfigObjectField.id.getOrElse(0)
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param transferInsertTransferRequestSalesforceTransferConfigObjectField TransferInsertTransferRequestSalesforceTransferConfigObjectField
   * @return 作成したレコードのID
   */
  private def insertTransferConfigSalesforceObjectField(userGroup: String, userId: String, transferConfigSalesforceObjectId: BigInt, transferInsertTransferRequestSalesforceTransferConfigObjectField: TransferInsertTransferRequestSalesforceTransferConfigObjectField): BigInt = {
    TransferConfigSalesforceObjectField(
      0,
      transferConfigSalesforceObjectId,
      transferInsertTransferRequestSalesforceTransferConfigObjectField.name,
      transferInsertTransferRequestSalesforceTransferConfigObjectField.label,
      transferInsertTransferRequestSalesforceTransferConfigObjectField.field_type,
      transferInsertTransferRequestSalesforceTransferConfigObjectField.active,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).insert
  }


  /**
   * MailTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return MailTransfer用のconfig
   */
  private def getTransferConfigMail(userGroup: String, transferConfigId: BigInt): Option[TransferGetTransferResponseMailTransferConfig] = {
    TransferConfigMail.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferResponseMailTransferConfig(
        f.id,
        f.transfer_config_id,
        f.use_cc,
        f.use_bcc,
        f.use_replyto,
        getTransferConfigMailAddress(userGroup, f.id)
      )
    })
  }

  /**
   * MailTransferConfigのメールアドレスリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @return MailTransferConfigに付随するメールアドレスリスト
   */
  private def getTransferConfigMailAddress(userGroup: String, transferConfigMailId: BigInt): List[TransferGetTransferResponseMailTransferConfigMailAddress] = {
    TransferConfigMailAddress.getList(userGroup, transferConfigMailId).map(f => {
      TransferGetTransferResponseMailTransferConfigMailAddress(
        f.id,
        f.transfer_config_mail_id,
        f.address_index,
        f.name,
        f.address
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return SalesforceTransfer用のconfig
   */
  private def getTransferConfigSalesforce(userGroup: String, transferConfigId: BigInt): Option[TransferGetTransferResponseSalesforceTransferConfig] = {
    TransferConfigSalesforce.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfig(
        f.id,
        f.transfer_config_id,
        f.sf_user_name,
        f.sf_password,
        f.sf_security_token,
        getTransferConfigSalesforceObject(userGroup, f.id)
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig Object 取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @return SalesforceTransfer用のconfig Object リスト
   */
  private def getTransferConfigSalesforceObject(userGroup: String, transferConfigSalesforceId: BigInt): List[TransferGetTransferResponseSalesforceTransferConfigObject] = {
    TransferConfigSalesforceObject.getList(userGroup, transferConfigSalesforceId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfigObject(
        f.id,
        f.transfer_config_salesforce_id,
        f.name,
        f.label,
        f.active,
        getTransferConfigSalesforceObjectField(userGroup, f.id)
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig Object Field 取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @return SalesforceTransfer用のconfig Object Field リスト
   */
  private def getTransferConfigSalesforceObjectField(userGroup: String, transferConfigSalesforceObjectId: BigInt): List[TransferGetTransferResponseSalesforceTransferConfigObjectField] = {
    TransferConfigSalesforceObjectField.getList(userGroup, transferConfigSalesforceObjectId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfigObjectField(
        f.id,
        f.transfer_config_salesforce_object_id,
        f.name,
        f.label,
        f.field_type,
        f.active
      )
    })
  }

  /*
  Update -> Insert変換
   */
  def TransferUpdateTransferRequestSalesforceTransferConfigObjectToTransferInsertTransferRequestSalesforceTransferConfigObject(src: TransferUpdateTransferRequestSalesforceTransferConfigObject): TransferInsertTransferRequestSalesforceTransferConfigObject = {
    TransferInsertTransferRequestSalesforceTransferConfigObject(
      src.transfer_config_salesforce_id,
      src.name,
      src.label,
      src.active,
      src.fields.map(f => TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(f))
    )
  }

  def TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(src: TransferUpdateTransferRequestSalesforceTransferConfigObjectField): TransferInsertTransferRequestSalesforceTransferConfigObjectField = {
    TransferInsertTransferRequestSalesforceTransferConfigObjectField(
      src.transfer_config_salesforce_object_id,
      src.name,
      src.label,
      src.field_type,
      src.active
    )
  }
}
