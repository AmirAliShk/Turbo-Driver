package ir.team_x.cloud_transport.taxi_driver.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogCallBinding
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.taxi_driver.utils.CallHelper
import ir.team_x.cloud_transport.taxi_driver.utils.KeyBoardHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil
import org.json.JSONObject

class CallDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogCallBinding

    fun show(number1: String, number2: String) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCallBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypeFaceUtil.overrideFont(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        binding.imgClose.setOnClickListener { dismiss() }
        binding.txtNumber1.text = number1
        binding.txtNumber2.text = number2

        binding.llNumber1.setOnClickListener {
            CallHelper.make(if (number1.startsWith("0")) number1 else "0$number1")
            dismiss()
        }

        binding.llNumber2.setOnClickListener {
            CallHelper.make(if (number2.startsWith("0")) number2 else "0$number2")
            dismiss()
        }

        dialog.show()

    }

    private fun finish(serviceId: Int, price: String) {
        RequestHelper.builder(EndPoint.FINISH)
            .listener(finishCallBack)
            .addParam("serviceId", serviceId)
            .addParam("price", price)
            .post()
    }

    private val finishCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        val result = dataArr.getJSONObject(0).getBoolean("result")
                        if (result) {
                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
                            dismiss()
                            MyApplication.currentActivity.onBackPressed()
                        }else{
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    }else{
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
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

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
            AvaCrashReporter.send(e, "FactorDialog class, dismiss method")
        }
    }

}