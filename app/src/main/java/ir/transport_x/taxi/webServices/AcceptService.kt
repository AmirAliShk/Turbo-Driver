package ir.transport_x.taxi.webServices

import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.dialog.AvailableServiceDialog
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.okHttp.RequestHelper
import org.json.JSONObject

class AcceptService {

    interface Listener {
        fun onSuccess(msg:String)
        fun onFailure()
    }

    lateinit var listener: Listener
    fun accept(serviceId: String, listener: Listener) {
        this.listener = listener
        RequestHelper.builder(EndPoint.ACCEPT)
            .addParam("serviceId", serviceId)
            .listener(acceptCallBack)
            .post()
    }

    private val acceptCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد.","data":[{"message":"سرویس توسط شخص دیگری پذیرفته شد","typeOut":5}]}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val msg = dataObj.getString("message")
                        val typeOut = dataObj.getInt("typeOut")
                        if(typeOut==1){
                            AvailableServiceDialog.dismiss()
                            UpdateCharge().update(object:UpdateCharge.ChargeListener{
                                override fun getCharge(charge: String, response: String) {
                                    MyApplication.prefManager.setCharge(charge)
                                }
                            })
                            listener.onSuccess(msg)
                        }else{
                            listener.onFailure()

                            GeneralDialog().message(msg).secondButton("باشه") {}.show()
                        }
                        //TODO check other type in this response
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                listener.onFailure()
            }
        }
    }
}