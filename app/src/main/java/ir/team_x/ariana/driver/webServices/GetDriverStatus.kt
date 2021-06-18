package ir.team_x.ariana.driver.webServices

import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.okHttp.RequestHelper
import org.json.JSONObject

class GetDriverStatus {

    fun getStatus() {
        RequestHelper.builder(EndPoint.STATUS)
            .listener(statusCallBack)
            .get()
    }

    private val statusCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if(success){
                        //TODO check other type in this response
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
            }
        }
    }
}