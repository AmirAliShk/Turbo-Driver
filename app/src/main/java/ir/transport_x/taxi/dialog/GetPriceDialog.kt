package ir.transport_x.taxi.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.DialogGetPriceBinding
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.UpdateCharge
import org.json.JSONObject

class GetPriceDialog {

    private lateinit var dialog: Dialog
    lateinit var binding: DialogGetPriceBinding

    interface FinishServiceListener {
        fun onFinishService(isFinish: Boolean)
    }

    private lateinit var finishServiceListener: FinishServiceListener

    fun show(serId: Int, finishServiceListener: FinishServiceListener) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGetPriceBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog.window!!.attributes = wlp
        dialog.setCancelable(false)

        this.finishServiceListener = finishServiceListener

        MyApplication.handler.postDelayed({
            KeyBoardHelper.showKeyboard(MyApplication.currentActivity)
        }, 200)

        binding.imgClose.setOnClickListener { dismiss() }

        StringHelper.setCommaOnTime(binding.edtPrice)

        binding.btnEndTrip.setOnClickListener {
            if (binding.edtPrice.text.trim()
                    .isEmpty() || binding.edtPrice.text.trim() == "0" || binding.edtPrice.text.trim().length < 2
            ) {
                binding.edtPrice.error = "مبلغ را به تومان وارد کنید"
                binding.edtPrice.requestFocus()
                return@setOnClickListener
            }
            GeneralDialog()
                .message("از اتمام سرویس اطمینان دارید؟")
                .firstButton("بله") {
                    finish(serId, binding.edtPrice.text.trim().toString())
                }
                .secondButton("خیر") {}
                .show()
        }

        dialog.show()
    }

    private fun finish(serviceId: Int, price: String) {
        binding.vfEndService.displayedChild = 1
        RequestHelper.builder(EndPoint.FINISH)
            .listener(finishCallBack)
            .addParam("serviceId", serviceId)
            .addParam("price", StringHelper.extractTheNumber(price))
            .post()
    }

    private val finishCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfEndService.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val result = dataObj.getBoolean("result")
                        if (result) {
                            UpdateCharge().update(object : UpdateCharge.ChargeListener {
                                override fun getCharge(charge: String, response: String) {
                                }
                            })
                            GeneralDialog().message(message).firstButton("باشه") {
                                MyApplication.handler.postDelayed({
                                    KeyBoardHelper.hideKeyboard()
                                },300)
                                MyApplication.currentActivity.onBackPressed()
                            }.show()
                            finishServiceListener.onFinishService(true)
                            dismiss()
                        } else {
                            finishServiceListener.onFinishService(false)
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } else {
                        finishServiceListener.onFinishService(false)
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfEndService.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfEndService.displayedChild = 0
            }
        }
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            MyApplication.handler.postDelayed({
                KeyBoardHelper.hideKeyboard()
            }, 50)

        } catch (e: Exception) {
            AvaCrashReporter.send(e, "getPriceDialog class, dismiss method")
        }
    }

}