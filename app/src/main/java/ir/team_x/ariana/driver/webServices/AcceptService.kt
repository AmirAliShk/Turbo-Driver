package ir.team_x.ariana.driver.webServices

import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.okHttp.RequestHelper
import org.json.JSONObject

class AcceptService {

    interface Listener {
        fun onSuccess()
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

                    if(success){
                        //TODO check other type in this response
                        listener.onSuccess()
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