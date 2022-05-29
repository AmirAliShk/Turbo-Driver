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
    var message = ""
    private lateinit var _firstButton: ButtonModel
    private lateinit var _secondButton: ButtonModel

    fun setCancelable(cancelable: Boolean): GeneralDialog {
        this.cancelable = cancelable
        return this
    }

    fun firstButton(text: String, runnable: Runnable?): GeneralDialog {
        _firstButton = ButtonModel(text, runnable)
        return this
    }

    fun secondButton(text: String, runnable: Runnable?): GeneralDialog {
        _secondButton = ButtonModel(text, runnable)
        return this
    }

    fun message(message: String): GeneralDialog {
        this.message = message
        return this
    }

    fun show() {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGeneralBinding.inflate(LayoutInflater.from(dialog.context))
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

        if (::_firstButton.isInitialized) {
            binding.btnPositive.text = _firstButton.text
        } else {
            binding.btnPositive.visibility = View.GONE
            binding.space.visibility = View.GONE
        }

        if (::_secondButton.isInitialized) {
            binding.btnNegative.text = _secondButton.text
        } else {
            binding.btnNegative.visibility = View.GONE
            binding.space.visibility = View.GONE
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
            _firstButton.body?.run()
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
            _secondButton.body?.run()
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

    data class ButtonModel(val text: String, val body: Runnable?)

}