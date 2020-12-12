package net.macolabo.sform2.services.Form

import java.time.ZonedDateTime
import java.util.UUID

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}

import scala.concurrent.ExecutionContext

class FormService @Inject() (implicit ex: ExecutionContext) {

  /**
   * フォーム詳細データ取得
   * @param identity 認証情報
   * @param hashed_form_id フォームhashed ID
   * @return フォームデータ
   */
  def getForm(identity: User, hashed_form_id: String): Option[FormGetFormResponse] = {
    val userGroup = identity.group.getOrElse("")
    Form.get(userGroup, hashed_form_id).map(f => {
      FormGetFormResponse(
        f.id,
        f.name,
        f.form_index,
        f.title,
        f.status,
        f.cancel_url,
        f.close_text.getOrElse(""),
        f.hashed_id,
        f.complete_url,
        f.input_header.getOrElse(""),
        f.complete_text.getOrElse(""),
        f.confirm_header.getOrElse(""),
        convertToFormGetFormResponseFormCol(userGroup, f.id),
        convertToFormGetFormResponseTransferTask(userGroup, f.id)
      )
    })
  }

  /**
   * フォームリスト取得
   * @param identity 認証情報
   * @return フォームリスト
   */
  def getList(identity: User): FormGetListResponse = {
    val userGroup = identity.group.getOrElse("")
    val forms = Form.getList(userGroup).map(f => {
      FormGetListForm(
        f.id,
        f.name,
        f.form_index,
        f.title,
        f.status,
        f.hashed_id
      )
    })
    FormGetListResponse(forms, forms.length)
  }

  /**
   * フォーム作成
   * @param identity 認証情報
   * @param formInsertFormRequest フォーム作成リクエストデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(identity: User, formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    val response = insertForm(identity, formInsertFormRequest)
    formInsertFormRequest.form_cols.map(f => {
      val formColId = insertFormCol(identity, f, response.id)
      f.select_list.map(s => {
        insertFormColSelect(identity, s, response.id, formColId)
      })
      insertFormColValidation(identity, f.validations, response.id, formColId)
    })
    response
  }

  /**
   * フォーム更新
   * @param identity 認証情報
   * @param formUpdateFormRequest フォーム更新リクエストデータ
   * @return フォーム更新結果レスポンス
   */
  def update(identity: User, formUpdateFormRequest: FormUpdateFormRequest): FormUpdateFormResponse = {
    val formId = updateForm(identity, formUpdateFormRequest)

    val updatedColIds = formUpdateFormRequest.form_cols.map(f => {
      f.id match {
        case Some(i: BigInt) if i > 0 => updateFormCol(identity, f)
        case _ => {
          val newColId = insertFormCol(identity, FormUpdateFormRequestFormColToFormInsertFormRequestFormCol(f), formUpdateFormRequest.id)
          f.select_list.map(s => {
            insertFormColSelect(identity, FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(s), formUpdateFormRequest.id, newColId)
          })
          insertFormColValidation(identity, FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(f.validations), formUpdateFormRequest.id, newColId)
          newColId
        }
      }
    })

    val updatedTransferTasks = formUpdateFormRequest.form_transfer_tasks.map(f => {
      f.id match {
        case Some(i: BigInt) if i > 0 => updateFormTransferTask(identity, f)
        case _ => insertFormTransferTask(identity, FormUpdateFormRequestFormTransferTaskToFormInsertFormRequestFormTransferTask(f), formUpdateFormRequest.id)
      }
    })

    // 削除対象のフォーム項目及び転送タスクを抽出、削除
    val userGroup = identity.group.getOrElse("")
    FormCol
        .getList(userGroup, formUpdateFormRequest.id)
        .filterNot(c => updatedColIds.contains(c.id) )
        .map(c => c.id)
        .foreach(c => FormCol.erase(userGroup, c))
    FormTransferTask
        .getList(userGroup, formUpdateFormRequest.id)
        .filterNot(c => updatedTransferTasks.contains(c.id))
        .map(c => c.id)
        .foreach(c => FormTransferTask.erase(userGroup, c))

    FormUpdateFormResponse(formId)
  }

  /**
   * フォーム削除
   * @param identity 認証情報
   * @param hashed_form_id ハッシュ化フォームID
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(identity: User, hashed_form_id: String): FormDeleteFormResponse = {
    val userGroup = identity.group.getOrElse("")
    val result = Form.get(identity.group.get, hashed_form_id).map(f => {
      FormCol.getList(userGroup, f.id).map(c => {
        FormColSelect.getList(userGroup, f.id, c.id).map(s => {
          FormColSelect.erase(userGroup, s.id)
        })
        FormColValidation.get(userGroup, f.id, c.id).map(v => {
          FormColValidation.erase(userGroup, v.id)
        })
        FormCol.erase(userGroup, c.id)
      })
      Form.erase(userGroup, f.id)
    })
    FormDeleteFormResponse(result)
  }

  //-------------------------------------------------
  //  フォーム詳細変換ロジック
  //-------------------------------------------------
  private def convertToFormGetFormResponseFormCol(userGroup: String, form_id: BigInt): List[FormGetFormResponseFormCol] = {
    FormCol.getList(userGroup, form_id).map(f => {
      FormGetFormResponseFormCol(
        f.id,
        f.form_id,
        f.name,
        f.col_id,
        f.col_index,
        f.col_type,
        f.default_value.getOrElse(""),
        convertToFormGetFormResponseFormColSelect(userGroup, form_id, f.id),
        convertToFormGetFormResponseFormColValidation(userGroup, form_id, f.id)
      )
    })
  }

  private def convertToFormGetFormResponseFormColSelect(userGroup: String, form_id: BigInt, form_col_id: BigInt): List[FormGetFormResponseFormColSelect] = {
    FormColSelect.getList(userGroup, form_id, form_col_id).map(f => {
      FormGetFormResponseFormColSelect(
        f.id,
        f.form_col_id,
        f.form_id,
        f.select_index,
        f.select_name,
        f.select_value,
        f.is_default,
        f.edit_style.getOrElse(""),
        f.view_style.getOrElse("")
      )
    })
  }

  private def convertToFormGetFormResponseFormColValidation(userGroup: String, form_id: BigInt, form_col_id: BigInt): Option[FormGetFormResponseFormColValidation] = {
    FormColValidation.get(userGroup, form_id, form_col_id).map(f => {
      FormGetFormResponseFormColValidation(
        f.id,
        f.form_col_id,
        f.form_id,
        f.max_value,
        f.min_value,
        f.max_length,
        f.min_length,
        f.input_type,
        f.required
      )
    })
  }

  private def convertToFormGetFormResponseTransferTask(userGroup: String, formId: BigInt): List[FormGetFormResponseFormTransferTask] = {
    FormTransferTask.getList(userGroup, formId).map(f => {
      FormGetFormResponseFormTransferTask(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        convertToFormGetFormResponseTransferTaskCondition(userGroup, formId, f.id),
        convertToFormGetFormResponseTransferTaskMail(userGroup, f.id),
        convertToFormGetFormResponseTransferTaskSalesforce(userGroup, f.id)
      )
    })
  }

  private def convertToFormGetFormResponseTransferTaskCondition(userGroup: String, formId: BigInt, formTransferTaskId: BigInt): List[FormGetFormResponseFormTransferTaskCondition] = {
    FormTransferTaskCondition.getList(userGroup, formId, formTransferTaskId).map(f => {
      FormGetFormResponseFormTransferTaskCondition(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value
      )
    })
  }

  private def convertToFormGetFormResponseTransferTaskMail(userGroup: String, formTransferTaskId: BigInt): Option[FormGetFormResponseFormTransferTaskMail] = {
    FormTransferTaskMail.get(userGroup, formTransferTaskId).map(f => {
      FormGetFormResponseFormTransferTaskMail(
        f.id,
        f.form_transfer_task_id,
        f.from_address_id,
        f.to_address,
        f.cc_address,
        f.bcc_address_id,
        f.replyto_address_id,
        f.subject,
        f.body
      )
    })
  }

  private def convertToFormGetFormResponseTransferTaskSalesforce(userGroup: String, formTransferTaskId: BigInt): Option[FormGetFormResponseFormTransferTaskSalesforce] = {
    FormTransferTaskSalesforce.get(userGroup, formTransferTaskId).map(f => {
      FormGetFormResponseFormTransferTaskSalesforce(
        f.id,
        f.form_transfer_task_id,
        f.object_name,
        convertToFormGetFormResponseTransferTaskSalesforceField(userGroup, f.id)
      )
    })
  }


  private def convertToFormGetFormResponseTransferTaskSalesforceField(userGroup: String, formTransferTaskSalesforceId: BigInt): List[FormGetFormResponseFormTransferTaskSalesforceField] = {
    FormTransferTaskSalesforceField.getList(userGroup, formTransferTaskSalesforceId).map(f => {
      FormGetFormResponseFormTransferTaskSalesforceField(
        f.id,
        f.form_transfer_task_salesforce_id,
        f.form_column_id,
        f.field_name
      )
    })
  }

  //-------------------------------------------------
  //  フォーム更新ロジック
  //-------------------------------------------------
  private def updateForm(identity: User, formUpdateFormRequest: FormUpdateFormRequest): BigInt = {
    val form = Form(
      formUpdateFormRequest.id,
      formUpdateFormRequest.hashed_id,
      formUpdateFormRequest.form_index,
      formUpdateFormRequest.name,
      formUpdateFormRequest.title,
      formUpdateFormRequest.status,
      formUpdateFormRequest.cancel_url,
      formUpdateFormRequest.complete_url,
      Option(formUpdateFormRequest.input_header),
      Option(formUpdateFormRequest.confirm_header),
      Option(formUpdateFormRequest.complete_text),
      Option(formUpdateFormRequest.close_text),
      "{}",
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    form.update
  }

  private def updateFormCol(identity: User, formUpdateFormRequestFormCol: FormUpdateFormRequestFormCol): BigInt = {
    val formCol = FormCol(
      formUpdateFormRequestFormCol.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormCol.form_id,
      formUpdateFormRequestFormCol.name,
      formUpdateFormRequestFormCol.col_id,
      formUpdateFormRequestFormCol.col_index,
      formUpdateFormRequestFormCol.col_type,
      Option(formUpdateFormRequestFormCol.default_value),
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formCol.update

    val updatedSelectListIds = formUpdateFormRequestFormCol.select_list.map(s => {
      s.id match {
        case Some(i: BigInt) if i > 0 => updateFormColSelect(identity, s)
        case _ => insertFormColSelect(identity, FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(s), formCol.form_id, formCol.id)
      }
    })

    // 削除対象のフォーム項目・選択項目IDを抽出、削除
    val userGroup = identity.group.getOrElse("")
    FormColSelect
      .getList(userGroup, formUpdateFormRequestFormCol.form_id, formUpdateFormRequestFormCol.id.getOrElse(0))
      .filterNot(c => updatedSelectListIds.contains(c.id))
      .map(c => c.id)
      .foreach(c => FormColSelect.erase(userGroup, c))

    formUpdateFormRequestFormCol.validations.id match {
      case Some(i: BigInt) if i > 0 => updateFormColValidation(identity, formUpdateFormRequestFormCol.validations)
      case _ => insertFormColValidation(identity, FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(formUpdateFormRequestFormCol.validations), formCol.form_id, formCol.id)
    }
    formCol.id
  }

  private def updateFormColSelect(identity: User, formUpdateFormRequestFormColSelect: FormUpdateFormRequestFormColSelect): BigInt = {
    val formColSelect = FormColSelect(
      formUpdateFormRequestFormColSelect.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormColSelect.form_col_id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormColSelect.form_id,
      formUpdateFormRequestFormColSelect.select_index,
      formUpdateFormRequestFormColSelect.select_name,
      formUpdateFormRequestFormColSelect.select_value,
      formUpdateFormRequestFormColSelect.is_default,
      Option(formUpdateFormRequestFormColSelect.edit_style),
      Option(formUpdateFormRequestFormColSelect.view_style),
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColSelect.update
    formColSelect.id
  }

  private def updateFormColValidation(identity: User, formUpdateFormRequestFormColValidation: FormUpdateFormRequestFormColValidation): BigInt = {
    val formColValidation = FormColValidation(
      formUpdateFormRequestFormColValidation.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormColValidation.form_col_id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormColValidation.form_id,
      formUpdateFormRequestFormColValidation.max_value,
      formUpdateFormRequestFormColValidation.min_value,
      formUpdateFormRequestFormColValidation.max_length,
      formUpdateFormRequestFormColValidation.min_length,
      formUpdateFormRequestFormColValidation.input_type,
      formUpdateFormRequestFormColValidation.required,
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColValidation.update
  }

  private def updateFormTransferTask(identity: User, formUpdateFormRequestFormTransferTask: FormUpdateFormRequestFormTransferTask): BigInt = {
    val formTransferTask = FormTransferTask(
      formUpdateFormRequestFormTransferTask.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTask.transfer_config_id,
      formUpdateFormRequestFormTransferTask.form_id,
      formUpdateFormRequestFormTransferTask.task_index,
      formUpdateFormRequestFormTransferTask.name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now(),
    )
    formTransferTask.update

    val updatedConditions = formUpdateFormRequestFormTransferTask.form_transfer_task_conditions.map(t => {
      t.id match {
        case Some(i: BigInt) if i>0 => updateFormTransferTaskCondition(identity, t)
        case _ => insertFormTransferTaskCondition(identity, FormUpdateFormRequestFormTransferTaskConditionToFormInsertFormRequestFormTransferTaskCondition(t), formTransferTask.id)
      }
    })

    val userGroup = identity.group.getOrElse("")
    FormTransferTaskCondition
      .getList(userGroup, formTransferTask.form_id, formTransferTask.id)
        .filterNot(c => updatedConditions.contains(c.id))
        .map(c => c.id)
        .foreach(c => FormTransferTaskCondition.erase(userGroup, c))

    formUpdateFormRequestFormTransferTask.mail.map(m => {
      m.id match {
        case Some(i: BigInt) if i>0 => updateFormTransferTaskMail(identity, m)
        case _ => insertFormTransferTaskMail(identity, FormUpdateFormRequestFormTransferTaskMailToFormInsertFormRequestFormTransferTaskMail(m), formTransferTask.id)
      }
    })

    formUpdateFormRequestFormTransferTask.salesforce.map(s => {
      s.id match {
        case Some(i: BigInt) if i>0 => updateFormTransferTaskSalesforce(identity, s)
        case _ => insertFormTransferTaskSalesforce(identity, FormUpdateFormRequestFormTransferTaskSalesforceToFormInsertFormRequestFormTransferTaskSalesforce(s), formTransferTask.id)
      }
    })

    formUpdateFormRequestFormTransferTask.id.getOrElse(0)
  }

  private def updateFormTransferTaskCondition(identity: User, formUpdateFormRequestFormTransferTaskCondition: FormUpdateFormRequestFormTransferTaskCondition): BigInt = {
    val formTransferTaskCondition = FormTransferTaskCondition(
      formUpdateFormRequestFormTransferTaskCondition.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskCondition.form_transfer_task_id,
      formUpdateFormRequestFormTransferTaskCondition.form_id,
      formUpdateFormRequestFormTransferTaskCondition.form_col_id,
      formUpdateFormRequestFormTransferTaskCondition.operator,
      formUpdateFormRequestFormTransferTaskCondition.cond_value,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskCondition.update
    formUpdateFormRequestFormTransferTaskCondition.id.getOrElse(BigInt(0))
  }

  private def updateFormTransferTaskMail(identity: User, formUpdateFormRequestFormTransferTaskMail: FormUpdateFormRequestFormTransferTaskMail): BigInt = {
    val formTransferTaskMail = FormTransferTaskMail(
      formUpdateFormRequestFormTransferTaskMail.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskMail.form_transfer_task_id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskMail.from_address_id,
      formUpdateFormRequestFormTransferTaskMail.to_address,
      formUpdateFormRequestFormTransferTaskMail.cc_address,
      formUpdateFormRequestFormTransferTaskMail.bcc_address_id,
      formUpdateFormRequestFormTransferTaskMail.replyto_address_id,
      formUpdateFormRequestFormTransferTaskMail.subject,
      formUpdateFormRequestFormTransferTaskMail.body,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskMail.update
    formUpdateFormRequestFormTransferTaskMail.id.getOrElse(BigInt(0))
  }

  private def updateFormTransferTaskSalesforce(identity :User, formUpdateFormRequestFormTransferTaskSalesforce: FormUpdateFormRequestFormTransferTaskSalesforce): BigInt = {
    val formTransferTaskSalesforce = FormTransferTaskSalesforce(
      formUpdateFormRequestFormTransferTaskSalesforce.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskSalesforce.form_transfer_task_id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskSalesforce.object_name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskSalesforce.update

    val updateFields = formUpdateFormRequestFormTransferTaskSalesforce.fields.map(f => {
      f.id match {
        case Some(i :BigInt) if i<0 => updateFormTransferTaskSalesforceField(identity, f)
        case _ => insertFormTransferTaskSalesforceField(identity, FormUpdateFormRequestFormTransferTaskSalesforceFieldToFormInsertFormRequestFormTransferTaskSalesforceField(f), formTransferTaskSalesforce.id)
      }
    })

    val userGroup = identity.group.getOrElse("")
    FormTransferTaskSalesforceField
      .getList(userGroup, formTransferTaskSalesforce.id)
        .filterNot(c => updateFields.contains(c.id))
        .map(c => c.id)
        .foreach(c => FormTransferTaskSalesforceField.erase(userGroup, c))

    formUpdateFormRequestFormTransferTaskSalesforce.id.getOrElse(0)
  }

  private def updateFormTransferTaskSalesforceField(identity: User, formUpdateFormRequestFormTransferTaskSalesforceField: FormUpdateFormRequestFormTransferTaskSalesforceField): BigInt = {
    val formTransferTaskSalesforceField = FormTransferTaskSalesforceField(
      formUpdateFormRequestFormTransferTaskSalesforceField.id.getOrElse(BigInt(0)),
      formUpdateFormRequestFormTransferTaskSalesforceField.form_transfer_task_salesforce_id,
      formUpdateFormRequestFormTransferTaskSalesforceField.form_column_id,
      formUpdateFormRequestFormTransferTaskSalesforceField.field_name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskSalesforceField.update
    formUpdateFormRequestFormTransferTaskSalesforceField.id.getOrElse(0)
  }

  //-------------------------------------------------
  //  フォーム作成ロジック
  //-------------------------------------------------
  private def insertForm(identity: User, formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    val hashedId = UUID.randomUUID().toString

    val form = Form(
      0,
      hashedId,
      formInsertFormRequest.form_index,
      formInsertFormRequest.name,
      formInsertFormRequest.title,
      formInsertFormRequest.status,
      formInsertFormRequest.cancel_url,
      formInsertFormRequest.complete_url,
      Option(formInsertFormRequest.input_header),
      Option(formInsertFormRequest.confirm_header),
      Option(formInsertFormRequest.complete_text),
      Option(formInsertFormRequest.close_text),
      "{}",
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    val formId = form.insert
    FormInsertFormResponse(formId, hashedId)
  }

  private def insertFormCol(identity: User, formInsertFormRequestFormCol: FormInsertFormRequestFormCol, formId: BigInt): BigInt = {
    val formCol = FormCol(
      0,
      formId,
      formInsertFormRequestFormCol.name,
      formInsertFormRequestFormCol.col_id,
      formInsertFormRequestFormCol.col_index,
      formInsertFormRequestFormCol.col_type,
      Option(formInsertFormRequestFormCol.default_value),
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formCol.insert
  }

  private def insertFormColSelect(identity: User, formInsertFormRequestFormColSelect: FormInsertFormRequestFormColSelect, formId: BigInt, formColId: BigInt): BigInt = {
    val formColSelect = FormColSelect(
      0,
      formColId,
      formId,
      formInsertFormRequestFormColSelect.select_index,
      formInsertFormRequestFormColSelect.select_name,
      formInsertFormRequestFormColSelect.select_value,
      formInsertFormRequestFormColSelect.is_default,
      Option(formInsertFormRequestFormColSelect.edit_style),
      Option(formInsertFormRequestFormColSelect.view_style),
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColSelect.insert
  }

  private def insertFormColValidation(identity: User, formInsertFormRequestFormColValidation: FormInsertFormRequestFormColValidation, formId: BigInt, formColId: BigInt): BigInt = {
    val formColValidation = FormColValidation(
      0,
      formColId,
      formId,
      formInsertFormRequestFormColValidation.max_value,
      formInsertFormRequestFormColValidation.min_value,
      formInsertFormRequestFormColValidation.max_length,
      formInsertFormRequestFormColValidation.min_length,
      formInsertFormRequestFormColValidation.input_type,
      formInsertFormRequestFormColValidation.required,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColValidation.insert
  }

  private def insertFormTransferTask(identity:User, formInsertFormRequestFormTransferTask: FormInsertFormRequestFormTransferTask, formId: BigInt): BigInt = {
    val formTransferTask = FormTransferTask(
      0,
      formInsertFormRequestFormTransferTask.transfer_config_id,
      formId,
      formInsertFormRequestFormTransferTask.task_index,
      formInsertFormRequestFormTransferTask.name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now(),
    )
    val formTransferTaskId = formTransferTask.insert

    formInsertFormRequestFormTransferTask.form_transfer_task_conditions.map(c => {
      insertFormTransferTaskCondition(identity, c, formTransferTaskId)
    })

    formInsertFormRequestFormTransferTask.mail.map(m => {
      insertFormTransferTaskMail(identity, m, formTransferTaskId)
    })

    formInsertFormRequestFormTransferTask.salesforce.map(s => {
      insertFormTransferTaskSalesforce(identity, s, formTransferTaskId)
    })

    formTransferTaskId
  }

  private def insertFormTransferTaskCondition(identity: User, formInsertFormRequestFormTransferTaskCondition: FormInsertFormRequestFormTransferTaskCondition, formTransferTaskId: BigInt): BigInt = {
    val formTransferTaskCondition = FormTransferTaskCondition(
      0,
      formTransferTaskId,
      formInsertFormRequestFormTransferTaskCondition.form_id,
      formInsertFormRequestFormTransferTaskCondition.form_col_id,
      formInsertFormRequestFormTransferTaskCondition.operator,
      formInsertFormRequestFormTransferTaskCondition.cond_value,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskCondition.insert
  }

  private def insertFormTransferTaskMail(identity: User, formInsertFormRequestFormTransferTaskMail: FormInsertFormRequestFormTransferTaskMail, formTransferTaskId: BigInt): BigInt = {
    val formTransferTaskMail = FormTransferTaskMail(
      0,
      formTransferTaskId,
      formInsertFormRequestFormTransferTaskMail.from_address_id,
      formInsertFormRequestFormTransferTaskMail.to_address,
      formInsertFormRequestFormTransferTaskMail.cc_address,
      formInsertFormRequestFormTransferTaskMail.bcc_address_id,
      formInsertFormRequestFormTransferTaskMail.replyto_address_id,
      formInsertFormRequestFormTransferTaskMail.subject,
      formInsertFormRequestFormTransferTaskMail.body,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskMail.insert
  }

  private def insertFormTransferTaskSalesforce(identity: User, formInsertFormRequestFormTransferTaskSalesforce: FormInsertFormRequestFormTransferTaskSalesforce, formTransferTaskId: BigInt): BigInt = {
    val formTransferTaskSalesforce = FormTransferTaskSalesforce(
      0,
      formTransferTaskId,
      formInsertFormRequestFormTransferTaskSalesforce.object_name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    val formTransferTaskSalesforceId = formTransferTaskSalesforce.insert
    formInsertFormRequestFormTransferTaskSalesforce.fields.map(f => {
      insertFormTransferTaskSalesforceField(identity, f, formTransferTaskSalesforceId)
    })
    formTransferTaskSalesforceId
  }

  private def insertFormTransferTaskSalesforceField(identity: User, formInsertFormRequestFormTransferTaskSalesforceField: FormInsertFormRequestFormTransferTaskSalesforceField, formTransferTaskSalesforceId: BigInt): BigInt = {
    val formTransferTaskSalesforceField = FormTransferTaskSalesforceField(
      0,
      formTransferTaskSalesforceId,
      formInsertFormRequestFormTransferTaskSalesforceField.form_column_id,
      formInsertFormRequestFormTransferTaskSalesforceField.field_name,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formTransferTaskSalesforceField.insert
  }


  //-------------------------------------------------
  //  更新リクエスト→作成リクエスト変換ロジック
  //-------------------------------------------------
  private def FormUpdateFormRequestFormColToFormInsertFormRequestFormCol(src: FormUpdateFormRequestFormCol): FormInsertFormRequestFormCol = {
    FormInsertFormRequestFormCol(
      src.name,
      src.col_id,
      src.col_index,
      src.col_type,
      src.default_value,
      src.select_list.map(s => FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(s)),
      FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(src.validations)
    )
  }

  private def FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(src: FormUpdateFormRequestFormColSelect): FormInsertFormRequestFormColSelect = {
    FormInsertFormRequestFormColSelect(
      src.select_index,
      src.select_name,
      src.select_value,
      src.is_default,
      src.edit_style,
      src.view_style
    )
  }

  private def FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(src: FormUpdateFormRequestFormColValidation): FormInsertFormRequestFormColValidation = {
    FormInsertFormRequestFormColValidation(
      src.max_value,
      src.min_value,
      src.max_length,
      src.min_length,
      src.input_type,
      src.required
    )
  }

  private def FormUpdateFormRequestFormTransferTaskToFormInsertFormRequestFormTransferTask(src: FormUpdateFormRequestFormTransferTask): FormInsertFormRequestFormTransferTask = {
    FormInsertFormRequestFormTransferTask(
      src.transfer_config_id,
      src.form_id,
      src.task_index,
      src.name,
      src.form_transfer_task_conditions.map(c => FormUpdateFormRequestFormTransferTaskConditionToFormInsertFormRequestFormTransferTaskCondition(c)),
      src.mail.map(m => FormUpdateFormRequestFormTransferTaskMailToFormInsertFormRequestFormTransferTaskMail(m)),
      src.salesforce.map(s => FormUpdateFormRequestFormTransferTaskSalesforceToFormInsertFormRequestFormTransferTaskSalesforce(s))
    )
  }

  private def FormUpdateFormRequestFormTransferTaskConditionToFormInsertFormRequestFormTransferTaskCondition(src: FormUpdateFormRequestFormTransferTaskCondition): FormInsertFormRequestFormTransferTaskCondition = {
    FormInsertFormRequestFormTransferTaskCondition(
      src.form_transfer_task_id,
      src.form_id,
      src.form_col_id,
      src.operator,
      src.cond_value
    )
  }

  private def FormUpdateFormRequestFormTransferTaskMailToFormInsertFormRequestFormTransferTaskMail(src: FormUpdateFormRequestFormTransferTaskMail): FormInsertFormRequestFormTransferTaskMail = {
    FormInsertFormRequestFormTransferTaskMail(
      src.form_transfer_task_id.getOrElse(0),
      src.from_address_id,
      src.to_address,
      src.cc_address,
      src.bcc_address_id,
      src.replyto_address_id,
      src.subject,
      src.body
    )
  }

  private def FormUpdateFormRequestFormTransferTaskSalesforceToFormInsertFormRequestFormTransferTaskSalesforce(src: FormUpdateFormRequestFormTransferTaskSalesforce): FormInsertFormRequestFormTransferTaskSalesforce = {
    FormInsertFormRequestFormTransferTaskSalesforce(
      src.form_transfer_task_id.getOrElse(0),
      src.object_name,
      src.fields.map(f => FormUpdateFormRequestFormTransferTaskSalesforceFieldToFormInsertFormRequestFormTransferTaskSalesforceField(f))
    )
  }

  private def FormUpdateFormRequestFormTransferTaskSalesforceFieldToFormInsertFormRequestFormTransferTaskSalesforceField(src: FormUpdateFormRequestFormTransferTaskSalesforceField): FormInsertFormRequestFormTransferTaskSalesforceField = {
    FormInsertFormRequestFormTransferTaskSalesforceField(
      src.form_transfer_task_salesforce_id,
      src.form_column_id,
      src.field_name
    )
  }
}

