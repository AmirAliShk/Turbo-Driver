package ir.team_x.ariana.operator.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.DialogGeneralBinding


data class Dialog(
    var title: String? = null,
    var message: String? = null,
    var negativeBtn: Runnable? = null,
    var positiveBtn: Runnable? = null,
    var cancelable: Boolean = false
) {

    private lateinit var dialog: Dialog
    lateinit var binding: DialogGeneralBinding

    fun title(title: String) = apply { this.title = title }
    fun message(message: String) = apply { this.message = message }
    fun negativeBtn(negativeRunnable: Runnable) = apply { this.negativeBtn = negativeRunnable }
    fun positiveBtn(positiveRunnable: Runnable) = apply { this.positiveBtn = positiveRunnable }
    fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

    fun show() {
        if (MyApplication.currentActivity.isFinishing) return

        dialog = Dialog(MyApplication.currentActivity)
        dialog.setContentView(binding.root)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.let { TypeFaceUtil.overrideFont(it.decorView) }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(cancelable)
//
//        binding.txtTitle.text = title
//        binding.txtMessage.text = message
//        binding.btnNegative.setOnClickListener { negativeBtn?.run() }
//        binding.btnPositive.setOnClickListener { positiveBtn?.run() }

        dialog.show()
    }

    fun dismiss() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
                dialog == null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}