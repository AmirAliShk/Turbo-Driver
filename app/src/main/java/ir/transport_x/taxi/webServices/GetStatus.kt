package ir.transport_x.taxi.webServices

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.transport_x.taxi.app.AppKeys
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.okHttp.RequestHelper
import org.json.JSONObject

class GetStatus {
    private var broadcaster: LocalBroadcastManager? = null

    fun getStatus(){
        broadcaster = LocalBroadcastManager.getInstance(MyApplication.context)
        RequestHelper.builder(EndPoint.STATUS)
            .listener(statusCallBack)
            .get()
    }

    private val statusCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد.","data":[{"active":1,"stationId":2,"distance":10,"stationName":"کلاهدوز","stationLat":36.29866,"stationLong":59.572666,"borderLimit":200}]}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val intent = Intent(AppKeys.BROADCAST_STATUS_KEY)
                        intent.putExtra(AppKeys.BROADCAST_STATUS_VALUE, dataObj.toString())
                        broadcaster?.sendBroadcast(intent)
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