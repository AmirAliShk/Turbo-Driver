package ir.transport_x.taxi.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.DialogGeneralBinding
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.TypeFaceUtil

class GeneralDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogGeneralBinding
    var cancelable = false
    var firstButtonText = ""
    var firstButtonRunnable: Runnable? = null
    var secondButtonText = ""
    var secondButtonRunnable: Runnable? = null
    var message = ""

    fun setCancelable(cancelable: Boolean): GeneralDialog {
        this.cancelable = cancelable
        return this
    }

    fun firstButton(text: String, runnable: Runnable?): GeneralDialog {
        this.firstButtonText = text
        this.firstButtonRunnable = runnable
        return this
    }

    fun secondButton(text: String, runnable: Runnable?): GeneralDialog {
        this.secondButtonText = text
        this.secondButtonRunnable = runnable
        return this
    }

    fun message(message: String): GeneralDialog {
        this.message = message
        return this
    }

    fun show() {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGeneralBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypeFaceUtil.overrideFont(binding.root, MyApplication.iranSansMediumTF)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.windowAnimations = R.style.ExpandAnimation
        dialog.window?.attributes = wlp
        dialog.setCancelable(cancelable)

        binding.txtMessage.text = message

        if (firstButtonRunnable == null && firstButtonText.isEmpty()) {
            binding.btnPositive.visibility = View.GONE
            binding.space.visibility = View.GONE
        }else{
            binding.btnPositive.text = firstButtonText
        }

        if (secondButtonRunnable == null && secondButtonText.isEmpty()) {
            binding.btnNegative.visibility = View.GONE
            binding.space.visibility = View.GONE
        }else{
            binding.btnNegative.text = secondButtonText
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
            firstButtonRunnable?.run()
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
            secondButtonRunnable?.run()
        }

        dialog.show()

    }

    fun dismiss() {
        try {
            if (::dialog.isInitialized)
                dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "GeneralDialog class, dismiss method")
        }
    }

}