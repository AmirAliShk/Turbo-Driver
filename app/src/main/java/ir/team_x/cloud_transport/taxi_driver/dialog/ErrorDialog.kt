package ir.team_x.cloud_transport.taxi_driver.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogErrorBinding
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil

class ErrorDialog {
    companion object {
        var dialog: Dialog? = null
        lateinit var binding: DialogErrorBinding
        var message: String = ""

        fun message(message: String): Companion {
            this.message = message
            return this
        }

        fun show() {
            dialog = Dialog(MyApplication.currentActivity)
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            binding = DialogErrorBinding.inflate(LayoutInflater.from(MyApplication.context))
            dialog?.setContentView(binding.root)
            TypeFaceUtil.overrideFont(binding.root, MyApplication.iranSansMediumTF)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val wlp: WindowManager.LayoutParams? = dialog?.window?.attributes
            wlp?.gravity = Gravity.CENTER
            wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
            wlp?.windowAnimations = R.style.ExpandAnimation
            dialog?.window?.attributes = wlp
            dialog?.setCancelable(false)

            binding.btnClose.setOnClickListener {
                dismiss()
            }

            binding.txtMessage.text = message.trim()

            dialog?.show()
        }

        fun dismiss() {
            try {
                if (dialog != null)
                    dialog?.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
                AvaCrashReporter.send(e, "ErrorDialog class, dismiss method")
            }
        }
    }

}