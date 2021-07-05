package ir.team_x.ariana.driver.webServices

import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.okHttp.RequestHelper
import org.json.JSONObject

class UpdateCharge {

    interface ChargeListener {
        fun getCharge(charge:String)
    }

    lateinit var listener: ChargeListener

    fun update(listener: ChargeListener) {
        this.listener = listener
        RequestHelper.builder(EndPoint.CHARGE)
            .listener(getChargeCallBack)
            .get()
    }

    private val getChargeCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val charge = dataObj.getString("charge")
                        listener.getCharge(charge)
                        MyApplication.prefManager.setCharge(charge)
                    }else{
                        listener.getCharge("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener.getCharge("")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                listener.getCharge("")
            }
        }
    }

}