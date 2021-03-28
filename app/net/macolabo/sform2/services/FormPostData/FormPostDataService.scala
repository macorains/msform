package net.macolabo.sform2.services.FormPostData

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.PostdataDAO
import net.macolabo.sform2.models.form.{Form, FormCol}
import play.api.libs.json.{JsArray, JsNull, JsObject, JsString, Json}

class FormPostDataService @Inject()(
  postdataDAO: PostdataDAO
                                   ){

  def getData(hashed_form_id: String, identity: User): String = {
    val formColMap = identity.group.map(group => {
      val form = Form.get(group, hashed_form_id)
      val colIdMap = form.map(f => {
        FormCol.getList(group, f.id).map(formCol => (formCol.col_id, formCol.name))
      })
      (form.map(f=>f.id), colIdMap)
    })

    val data = formColMap.flatMap(fcm => {
      fcm._1.flatMap(f => {
        fcm._2.map(cim => {
          val datalist = JsArray(postdataDAO.getPostdata(hashed_form_id).map(pd => {
            JsObject(cim.map(col => {
              col._1 -> (pd.postdata \ col._1).getOrElse(JsNull)
            }))
          }))
          (cim, datalist)
        })
      })
    }).getOrElse((List.empty, JsArray(List.empty)))

    val result = JsObject(Seq(
      "header" -> JsObject(data._1.map(x => (x._1, JsString(x._2)))),
      "data" -> data._2
    ))
    Json.stringify(result)
  }
}
