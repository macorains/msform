package models.daos

import java.io.StringWriter
import java.util.{ Date, UUID }
import javax.xml.parsers.{ DocumentBuilder, DocumentBuilderFactory }
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{ OutputKeys, Transformer, TransformerFactory }

import scala.collection.{ Map, Seq }
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory
import org.w3c.dom.{ Document, Element }
import play.api.libs.json._
import scalikejdbc._
import models.{ RsResultSet, User }
import utils.forms.FormParts

class FormsDAO extends FormParts {

  /**
   * フォームデータ
   * @param id
   * @param hashed_id
   * @param form_data
   */
  case class FormData(id: Int, hashed_id: String, form_data: String, user_group: String)
  object FormData extends SQLSyntaxSupport[FormData] {
    override val tableName = "d_form"
    def apply(rs: WrappedResultSet): FormData = {
      FormData(rs.int("id"), rs.string("hashed_id"), rs.string("form_data"), rs.string("user_group"))
    }
  }

  /**
   * フォーム送信データ
   * @param postdata_id
   * @param form_hashed_id
   * @param postdata
   */
  case class FormPostData(postdata_id: Int, form_hashed_id: String, postdata: String)
  object FormPostData extends SQLSyntaxSupport[FormPostData] {
    override val tableName = "d_postdata"
    def apply(rs: WrappedResultSet): FormPostData = {
      FormPostData(rs.int("postdata_id"), rs.string("form_hashed_id"), rs.string("postdata"))
    }
  }
  case class FormPostDataJson(postdata_id: Int, form_hashed_id: String, postdata: String)
  object FormPostDataJson {
    implicit def jsonFormPostDataJsonWrites: Writes[FormPostDataJson] = Json.writes[FormPostDataJson]
    implicit def jsonFormPostDataJsonReads: Reads[FormPostDataJson] = Json.reads[FormPostDataJson]
  }

  /**
   * フォーム項目定義
   * @param index 表示順
   * @param name フォーム項目名
   * @param colId フォーム項目ID
   * @param coltype フォーム項目種別
   * 　　　　　　　1 : テキスト            2 : コンボボックス      3 : チェックボックス    4 : ラジオボタン
   * 5 : テキストエリア      6 : 隠しテキスト        7 : 表示テキスト（非入力項目）
   * @param default 初期値
   * @param validations バリデーション定義リスト
   * @param selectList 選択肢定義リスト（ラジオ、チェックボックス、セレクトリスト用）
   */
  case class FormDefCol(index: String, name: String, colId: String, coltype: Option[String], default: Option[String], validations: JsObject, selectList: JsObject)
  object FormDefCol {
    implicit def jsonFormDefColWrites: Writes[FormDefCol] = Json.writes[FormDefCol]
    implicit def jsonFormDefColReads: Reads[FormDefCol] = Json.reads[FormDefCol]
  }

  /**
   * フォーム定義
   * @param index 表示順
   * @param id フォームID
   * @param status フォーム状態
   * @param name フォーム名
   * @param title フォームタイトル
   * @param extLink1 外部連携1使用フラグ（当面はSF専用）
   * @param cancelUrl キャンセル時遷移先URL
   * @param completeUrl  完了時遷移先URL
   * @param inputHeader フォーム入力画面ヘッダ文面
   * @param confirmHeader フォーム確認画面ヘッダ文面
   * @param completeText フォーム完了画面文面
   * @param formCols フォーム項目定義
   * @param closeText
   * @param replymailFrom
   * @param replymailSubject
   * @param replymailText,
   * @param noticemailSend
   * @param noticemailText
   */

  case class FormDef(index: String, id: Option[String], hashed_id: Option[String], status: String, name: String, title: String,
    extLink1: Option[Boolean], cancelUrl: String, completeUrl: String, inputHeader: String,
    confirmHeader: String, completeText: String, closeText: Option[String], replymailFrom: Option[String],
    replymailSubject: Option[String], replymailText: Option[String], noticemailSend: Option[String], noticemailText: Option[String], formCols: JsObject) {
    def replaceId(newId: Int, newHashedId: String): FormDef = {
      FormDef(index, Option(newId.toString), Option(newHashedId), status, name, title, extLink1, cancelUrl, completeUrl, inputHeader, confirmHeader, completeText,
        closeText, replymailFrom, replymailSubject, replymailText, noticemailSend, noticemailText, formCols)
    }
  }
  object FormDef {
    implicit def jsonFormDefWrites: Writes[FormDef] = Json.writes[FormDef]
    implicit def jsonFormDefReads: Reads[FormDef] = Json.reads[FormDef]
  }

  /**
   * フォームバリデーション定義
   * @param inputType 入力種別
   * @param minValue 最小値
   * @param maxValue 最大値
   * @param minLength 最小文字数
   * @param maxLength 最大文字数
   * @param required 必須項目
   */
  case class FormDefColValidation(inputType: String, minValue: String, maxValue: String, minLength: String, maxLength: String, required: Option[Boolean])
  object FormDefColValidation {
    implicit def jsonFormDefColValidationWrites: Writes[FormDefColValidation] = Json.writes[FormDefColValidation]
    implicit def jsonFormDefColValidationReads: Reads[FormDefColValidation] = Json.reads[FormDefColValidation]
  }

  /**
   * 選択リスト定義
   * @param index 表示順
   * @param displayText 表示文字列
   * @param value 値
   * @param default デフォルト選択状態にするか
   * @param viewStyle 確認画面時スタイル定義
   * @param editStyle 入力画面時スタイル定義
   */
  case class FormDefColSelectList(index: String, displayText: String, value: String, default: String, viewStyle: String, editStyle: String)
  object FormDefColSelectList {
    implicit def jsonFormDefColSelectListWrites: Writes[FormDefColSelectList] = Json.writes[FormDefColSelectList]
    implicit def jsonFormDefColSelectListReads: Reads[FormDefColSelectList] = Json.reads[FormDefColSelectList]
  }

  /**
   * レスポンスJSON用クラス
   * @param id id
   */
  case class insertFormResponce(id: String)
  object insertFormResponce {
    implicit def jsonInsertFormResponceWrites: Writes[insertFormResponce] = Json.writes[insertFormResponce]
    implicit def jsonInsertFormResponceReads: Reads[insertFormResponce] = Json.reads[insertFormResponce]
  }

  /**
   * バリデーション要求クラス
   * @param formid フォームのhashed_id
   * @param postdata フォーム送信データ
   */
  case class FormSaveRequest(formid: String, postdata: JsValue)
  object FormSaveRequest {
    implicit def jsonFormSaveRequestWrites: Writes[FormSaveRequest] = Json.writes[FormSaveRequest]
    implicit def jsonFormSaveRequestReads: Reads[FormSaveRequest] = Json.reads[FormSaveRequest]
  }

  /**
   * フォーム送信データ取得リクエスト用クラス
   * @param formid
   */
  case class FormPostDataRequest(formid: String)
  object FormPostDataRequest {
    implicit def jsonFormPostDataRequestWrites: Writes[FormPostDataRequest] = Json.writes[FormPostDataRequest]
    implicit def jsonFormPostDataRequestReads: Reads[FormPostDataRequest] = Json.reads[FormPostDataRequest]
  }

  /**
   * フォーム一覧
   * @return RsResultSet
   */
  def getList(identity: User): RsResultSet = {
    val userGroup = identity.group.getOrElse("")
    val f = FormData.syntax("f")
    DB localTx { implicit s =>
      val formDataList =
        withSQL(
          select(f.id, f.hashed_id, f.form_data, f.user_group)
            .from(FormData as f)
            .where
            .eq(f.user_group, userGroup)
        ).map(rs => FormData(rs)).list.apply()

      val jsString = formDataList.zipWithIndex.map {
        case (a, b) =>
          val formDefResult: JsResult[FormDef] = Json.parse(a.form_data).validate[FormDef]
          formDefResult match {
            case s: JsSuccess[FormDef] =>
              val f: FormDef = s.get.replaceId(a.id, a.hashed_id)
              "\"" + b + "\":" + Json.toJson(f)
            case _ =>
              "\"" + b + "\":"
          }
      }.mkString("{", ",", "}")
      RsResultSet("OK", "OK", Json.toJson(jsString))
    }
  }

  /**
   * フォーム送信データ取得
   * @param dt 入力データ
   * @return RsResultSet
   */
  def getData(dt: JsValue): RsResultSet = {
    val req: JsResult[FormPostDataRequest] = dt.validate[FormPostDataRequest]
    val f0 = FormData.syntax("f0")
    val f = FormPostData.syntax("f")
    req match {
      case s: JsSuccess[FormPostDataRequest] =>
        DB localTx { implicit l =>
          val formData =
            withSQL {
              select(f0.id, f0.hashed_id, f0.form_data)
                .from(FormData as f0)
                .where
                .eq(f0.hashed_id, "11")
            }.map(rs => FormData(rs)).single.apply().getOrElse(None)

          val formCols: Map[String, String] = {
            formData match {
              case c: FormData => {
                Json.parse(c.form_data).validate[FormDef] match {
                  case s: JsSuccess[FormDef] => {
                    val formDefColValue = s.get.formCols.value
                    formDefColValue.map({
                      case (k, v) => {
                        val formDefColResult: JsResult[FormDefCol] = v.validate[FormDefCol]
                        formDefColResult match {
                          case f: JsSuccess[FormDefCol] => (f.get.colId.toString, f.get.name.toString)
                          case e: JsError => ("", "")
                        }
                      }
                    }).toMap
                  }
                  case e: JsError => Map.empty[String, String]
                }
              }
              case _ => Map.empty[String, String]
            }
          }

          println(formCols)

          val postData: Seq[FormPostDataJson] =
            withSQL {
              select(f.postdata_id, f.form_hashed_id, f.postdata)
                .from(FormPostData as f)
                .where
                .eq(f.form_hashed_id, s.get.formid)
            }.map(rs => FormPostData(rs)).list().apply().map(f => FormPostDataJson(f.postdata_id, f.form_hashed_id, f.postdata))
          // RsResultSet("OK", "OK", Json.toJson(postData))
          RsResultSet("OK", Json.toJson(formCols).toString(), Json.toJson(postData))
        }
      case _ =>
        RsResultSet("NG", "NG", Json.parse("""{}"""))
    }
  }

  /**
   * フォームHTML取得
   * @param dt 入力データ
   * @return RsResultSet
   */
  def getHtml(dt: JsValue, host: String): RsResultSet = {
    println(dt.toString())
    val formId = (dt \ "formid").asOpt[String]
    val receiverPath = (dt \ "receiverPath").asOpt[String]
    formId match {
      case Some(id) => {
        val f = FormData.syntax("f")
        DB localTx { implicit s =>
          val formData =
            withSQL {
              select(f.id, f.hashed_id, f.form_data, f.user_group)
                .from(FormData as f)
                .where
                .eq(f.hashed_id, formId)
            }.map(rs => FormData(rs)).single.apply()
          formData match {
            case Some(s: FormData) =>
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(id, s, cFormMode.LOAD, None, host, receiverPath.getOrElse(""))))
            case _ =>
              RsResultSet("NG", "NG", Json.toJson("Could not get Formdata."))
          }
        }
      }
      case None => RsResultSet("NG", "NG", Json.toJson("Invalid Parameter."))
    }
  }

  /**
   * フォーム定義データをHTMLに変換
   * @param fd フォーム定義データ
   * @return HTML文字列
   */
  def convertFormDefToHtml(hashed_id: String, fd: FormData, mode: Int, postdata: Option[JsValue], host: String, receiverPath: String): String = {
    val formDefResult: JsResult[FormDef] = Json.parse(fd.form_data).validate[FormDef]
    formDefResult match {
      case s: JsSuccess[FormDef] =>
        val formDefColValue = s.get.formCols.value
        val validateResult: Map[String, String] = validateCols(formDefColValue, postdata).filter(p => p._2.length > 0)

        val htmlStr: Seq[String] =
          if (mode == cFormMode.REGIST) {
            savePostData(hashed_id, postdata.getOrElse(Json.toJson("")))
          } else {
            formDefColValue.map({
              case (k, v) =>
                val formDefColResult: JsResult[FormDefCol] = v.validate[FormDefCol]

                (k.toInt, formDefColResult match {
                  case f: JsSuccess[FormDefCol] =>
                    mode match {
                      case m: Int if m == cFormMode.LOAD => getColHtml(f.get, Map.empty[String, String])
                      case c: Int if c == cFormMode.CONFIRM =>
                        if (validateResult.nonEmpty) {
                          getColHtml(f.get, validateResult)
                        } else {
                          getConfirmColHtml(f.get, postdata.getOrElse(Json.toJson("")))
                        }
                    }
                  case e: JsError =>
                    println("Error1")
                    e.toString()
                })
            }).toSeq.sortBy(_._1).map(_._2)
          }
        val resultStr = if (validateResult.nonEmpty) "NG" else "OK"
        header(mode, s.get) + htmlStr.mkString("", "", "") + hashed_id_hidden(hashed_id) + validate_result_hidden(resultStr) + buttons(mode, validateResult.nonEmpty) + script1(host, receiverPath)
      case e: JsError =>
        println("Error1")
        println(e.toString())
        ""
    }

  }

  /**
   * データ作成
   * @param dt 入力データ
   * @return RsResultSet
   */
  def insert(dt: JsValue, identity: User): RsResultSet = {
    println("insert")
    val formDefData = (dt \ "formDef").as[JsValue]
    formDefData.validate[FormDef] match {
      case s: JsSuccess[FormDef] =>
        s.get.id match {
          case i: Option[String] =>
            i match {
              case Some(j) if j.length > 0 => RsResultSet("OK", "OK", updateForm(formDefData, j, identity))
              case _ => RsResultSet("OK", "OK", insertForm(formDefData, identity))
            }
          case _ => RsResultSet("OK", "OK", insertForm(formDefData, identity))
        }
      case e: JsError =>
        RsResultSet("NG", "JSON Error.", Json.toJson(e.toString))
    }
  }

  /**
   * データ更新
   * @param dt 入力データ
   * @return RsResultSet
   */
  def update(dt: JsValue): RsResultSet = {
    RsResultSet("OK", "OK", Json.parse("""{}"""))
  }

  /**
   * データ削除
   * @param dt 入力データ
   * @return RsResultSet
   */
  def delete(dt: JsValue): RsResultSet = {
    val id = (dt \ "id").as[String]
    val f = FormData.syntax("f")
    DB localTx { implicit s =>
      withSQL {
        deleteFrom(FormData)
          .where
          .eq(FormData.column.id, id)
      }.update.apply()
    }

    RsResultSet("NG", "NG", Json.parse("""{}"""))
  }

  /**
   * フォームバリデーション
   * @param dt Ajaxからの送信データ
   * @return
   */
  def validate(dt: JsValue, host: String): RsResultSet = {
    val receiverPath = (dt \ "receiverPath").asOpt[String].getOrElse("")
    println("validate!")
    println(dt.toString())
    val f = FormData.syntax("f")
    val formSaveRequest: JsResult[FormSaveRequest] = dt.validate[FormSaveRequest]
    formSaveRequest match {
      case s: JsSuccess[FormSaveRequest] =>
        DB localTx { implicit l =>
          val formData = withSQL {
            select(f.id, f.hashed_id, f.form_data, f.user_group)
              .from(FormData as f)
              .where
              .eq(f.hashed_id, s.get.formid)
          }.map(rs => FormData(rs)).single.apply()
          formData match {
            case Some(d) =>
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(s.get.formid, d, cFormMode.CONFIRM, Option(s.get.postdata), host, receiverPath)))
            case _ => RsResultSet("NG", "NG", Json.toJson("error"))
          }
        }
      case _ =>
        RsResultSet("NG", "NG", Json.toJson("error"))
    }
  }

  /**
   * フォーム送信データ保存
   * @param dt Ajaxからの送信データ
   * @return
   */
  def savePost(dt: JsValue, host: String): RsResultSet = {
    println("save!")
    println(dt.toString())
    val f = FormData.syntax("f")
    val formSaveRequest: JsResult[FormSaveRequest] = dt.validate[FormSaveRequest]
    formSaveRequest match {
      case s: JsSuccess[FormSaveRequest] =>
        DB localTx { implicit l =>
          val formData = withSQL {
            select(f.id, f.hashed_id, f.form_data, f.user_group)
              .from(FormData as f)
              .where
              .eq(f.hashed_id, s.get.formid)
          }.map(rs => FormData(rs)).single.apply()
          formData match {
            case Some(d) =>
              println("save!!")
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(s.get.formid, d, cFormMode.REGIST, Option(s.get.postdata), host, "")))
            case _ =>
              println("NG!!")
              RsResultSet("NG", "NG", Json.toJson("error"))
          }
        }
      case _ =>
        println("NG!!!!!")
        RsResultSet("NG", "NG", Json.toJson("error"))
    }
  }

  /**
   * 各項目についてバリデーション実行
   * @param fd フォーム定義
   * @param pd 受信データ
   * @return
   */
  private def validateCols(fd: Map[String, JsValue], pd: Option[JsValue]): Map[String, String] = {
    pd match {
      case Some(p: JsValue) =>
        fd map {
          case (k, v) =>
            v.validate[FormDefCol] match {
              case s: JsSuccess[FormDefCol] =>
                (s.get.colId, checkValidateRule(s.get, p))
              case e: JsError => ("", "")
            }
        }
      case _ =>
        Map.empty[String, String]
    }
  }

  /**
   * 各フォーム項目毎のバリデーション結果を返す
   * @param formDefCol フォーム項目定義
   * @param postdata 受信データ
   * @return
   */
  private def checkValidateRule(formDefCol: FormDefCol, postdata: JsValue): String = {

    formDefCol.validations.validate[FormDefColValidation] match {
      case f: JsSuccess[FormDefColValidation] =>
        (postdata \ formDefCol.colId) match {
          case v: JsLookupResult =>

            f.get.inputType match {
              case cFormValidationType.NUMBER =>
                if (v.as[String].matches("""[0-9]*""")) "" else cValidationErrorMessage.NOT_NUMBER_ERROR
              case cFormValidationType.ALPHANUM =>
                if (v.as[String].matches("""[0-9a-zA-Z]*""")) "" else cValidationErrorMessage.NOT_ALPHA_NUMBER_ERROR
              case cFormValidationType.KATAKANA =>
                if (v.as[String].matches("[\\u30A1-\\u30FA]*")) "" else cValidationErrorMessage.NOT_KATAKANA_ERROR
              case cFormValidationType.HIRAGANA =>
                if (v.as[String].matches("[\\u3041-\\u3096]*")) "" else cValidationErrorMessage.NOT_HIRAGANA_ERROR
              case cFormValidationType.EMAIL =>
                if (v.as[String].matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) "" else cValidationErrorMessage.EMAIL_ADDRESS_FORMAT_ERROR
              case cFormValidationType.POSTCODE => { "" }
              case _ => ""
            }
          case _ => {
            println("ppp")
            println((postdata \ formDefCol.name))
            ""
          }
        }
      case e: JsError => {
        println("jserror")
        println(formDefCol.validations)
        println(e)
        ""
      }
    }
  }

  /**
   * フォームデータ挿入
   * @param dt 入力データ
   * @return 処理結果のJsValue
   */
  private def insertForm(dt: JsValue, identity: User): JsValue = {
    println("insertForm")

    DB localTx { implicit s =>

      // 次のID取得
      val id: Option[Long] =
        sql"""SELECT auto_increment
             FROM information_schema.tables
             WHERE table_name = 'd_form'""".map(_.long(1)).single.apply()

      id match {
        case Some(l: Long) =>
          val rcdataResult: JsResult[FormDef] = dt.validate[FormDef]
          val hashed_id: String = UUID.randomUUID().toString
          val f: FormDef = rcdataResult.get.replaceId(l.intValue(), hashed_id)
          val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
          val newid: Long =
            sql"""INSERT INTO D_FORM(FORM_DATA,HASHED_ID,USER_GROUP,CREATED_USER,CREATED)
                 VALUES(${Json.toJson(f).toString},${hashed_id},${identity.group},${identity.userID.toString},${now})"""
              .updateAndReturnGeneratedKey.apply()
          Json.parse("""{"id": """" + newid.toString + """"}""")
        case None =>
          Json.parse("""{"id": "failed"}""")
      }
    }
  }

  /**
   * フォームデータ更新
   * @param dt 入力データ
   * @param formId フォームID
   * @return 処理結果のJsValue
   */
  private def updateForm(dt: JsValue, formId: String, identity: User): JsValue = {
    DB localTx { implicit s =>
      sql"""UPDATE D_FORM
           SET FORM_DATA=${dt.toString},
           MODIFIED_USER=${identity.userID.toString},
           MODIFIED=now() WHERE ID=${formId}""".update.apply;
      Json.parse("""{"id": """" + formId.toString + """"}""")
    }
  }

  /**
   * フォーム項目定義からHTML生成
   * @param colDef 項目定義データ
   * @return
   */
  private def getColHtml(colDef: FormDefCol, validateResult: Map[String, String]): String = {
    val dbfactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val docbuilder: DocumentBuilder = dbfactory.newDocumentBuilder()
    val document: Document = docbuilder.newDocument()
    val coltype = colDef.coltype.getOrElse("");

    val root: Element = document.createElement("div")
    root.setAttribute("class", coltype match {
      case "6" => "sform-col-none"
      case "7" => "sform-col-disptext"
      case _ => "sform-col"
    })

    val colName: Element = document.createElement("div")
    colName.setAttribute("class", coltype match {
      case "6" | "7" => "sform-col-name-none"
      case _ => "sform-col-name"
    })
    val colForm: Element = document.createElement("div")
    colForm.setAttribute("class", "sform-col-form")

    colName.setTextContent(colDef.name)
    colForm.appendChild(getColFormHtml(colDef, document))

    val colError: Element = document.createElement("span")
    colError.setAttribute("class", "sform-col-error")
    colError.setTextContent(validateResult.getOrElse(colDef.colId, ""))
    colForm.appendChild(colError)

    root.appendChild(colName)
    root.appendChild(colForm)

    val sb: StringWriter = new StringWriter()
    val tfactory: TransformerFactory = TransformerFactory.newInstance()
    val transformer: Transformer = tfactory.newTransformer()

    // HTML形式で出力する（XMLのままだとXML宣言がつく）
    transformer.setOutputProperty(OutputKeys.METHOD, "html")
    // インデントオン、かつインデントのスペース数を決める
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2")
    transformer.transform(new DOMSource(root), new StreamResult(sb))
    sb.toString()

  }

  /**
   * 項目種別からフォーム入力用タグを返す
   * @param colDef 項目定義データ
   * @param doc HTMLドキュメントオブジェクト
   * @return
   */
  private def getColFormHtml(colDef: FormDefCol, doc: Document): Element = {
    val typ: String = colDef.coltype match {
      case Some(s: String) => s
      case None => ""
    }

    val elem: Element = typ match {
      case "1" | "6" => doc.createElement("input")
      case "2" => doc.createElement("select")
      case "3" | "4" | "7" => doc.createElement("span")
      case "5" => doc.createElement("textarea")
      case _ => doc.createElement("span")
    }
    elem.setAttribute("class", typ match {
      case "1" | "2" | "5" | "6" => "sform-col-form-text"
      case "3" => "sform-col-form-checkbox"
      case "4" => "sform-col-form-radio"
      case "7" => "sform-col-form-span"
    })

    typ match {
      case "6" => {
        elem.setAttribute("type", "hidden")
      }
      case "7" => {
        elem.setTextContent(colDef.default.getOrElse(""))
      }
      case _ => None
    }

    elem.setAttribute("id", colDef.colId)

    colDef.selectList.value.map({
      case (k, v) =>
        v.validate[FormDefColSelectList] match {
          case s: JsSuccess[FormDefColSelectList] =>
            val element = typ match {
              case "2" => doc.createElement("option")
              case "3" | "4" => doc.createElement("input")
              case _ => doc.createElement("span")
            }
            element.setAttribute("type", typ match {
              case "3" => "checkbox"
              case "4" => "radio"
              case _ => ""
            })
            element.setAttribute("name", typ match {
              case "3" | "4" => "sel_" + colDef.colId
              case _ => ""
            })
            element.setTextContent(typ match {
              case "2" | "3" | "4" => s.get.displayText
              case _ => ""
            })
            element.setAttribute("value", s.get.value)
            element.setAttribute("id", colDef.colId + k)
            elem.appendChild(element)
          case e: JsError =>
            println(e.toString)
        }
    })
    elem
  }

  private def getConfirmColHtml(colDef: FormDefCol, postdata: JsValue): String = {
    val dbfactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val docbuilder: DocumentBuilder = dbfactory.newDocumentBuilder()
    val document: Document = docbuilder.newDocument()
    val colType = colDef.coltype.getOrElse("")
    val root: Element = document.createElement("div")

    colType match {
      case "6" | "7" => {
        root.setAttribute("class", "sform-col-none")
      }
      case _ => {
        root.setAttribute("class", "sform-col")
        val colName: Element = document.createElement("div")
        colName.setAttribute("class", "sform-col-name")
        val colForm: Element = document.createElement("div")
        colForm.setAttribute("class", "sform-col-form")
        colName.setTextContent(colDef.name)

        val postdata_text = (postdata \ colDef.colId).getOrElse(Json.toJson("")).as[String]
        val displayText = colType match {
          case "2" | "3" | "4" => {
            // ラジオ・チェックボックス・コンボの場合はラベルを取ってくる
            val listDef = (colDef.selectList \ postdata_text).asOpt[JsValue]
            listDef match {
              case Some(s) => (s \ "displayText").asOpt[String].getOrElse("")
              case None => ""
            }
          }
          case _ => postdata_text
        }
        colForm.setTextContent(displayText)

        root.appendChild(colName)
        root.appendChild(colForm)
      }
    }

    val sb: StringWriter = new StringWriter()
    val tfactory: TransformerFactory = TransformerFactory.newInstance()
    val transformer: Transformer = tfactory.newTransformer()

    // HTML形式で出力する（XMLのままだとXML宣言がつく）
    transformer.setOutputProperty(OutputKeys.METHOD, "html")
    // インデントオン、かつインデントのスペース数を決める
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2")
    transformer.transform(new DOMSource(root), new StreamResult(sb))
    sb.toString()
  }

  private def savePostData(hashed_id: String, dt: JsValue): Seq[String] = {
    val f = FormData.syntax("f")
    DB localTx { implicit l =>
      val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
      val newid: Long = sql"INSERT INTO D_POSTDATA(FORM_HASHED_ID,POSTDATA,CREATED,MODIFIED) VALUES(${hashed_id},${Json.toJson(dt).toString},${now},${now})"
        .updateAndReturnGeneratedKey.apply()
      Seq("")
    }
  }

  def header(mode: Int, formDef: FormDef): String = {
    val text: String = mode match {
      case cFormMode.LOAD => formDef.inputHeader
      case cFormMode.CONFIRM => formDef.confirmHeader
      case cFormMode.REGIST => formDef.completeText
    }
    val subClass: String = mode match {
      case cFormMode.LOAD => "sform-header-input"
      case cFormMode.CONFIRM => "sform-header-confirm"
      case cFormMode.REGIST => "sform-header-complete"
    }
    s"""
       |<div class="sform-header-div ${subClass}">
       |${text}
       |</div>
    """.stripMargin
  }

}