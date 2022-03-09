package ir.transport_x.taxi.dialog

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.DialogOverlayPermissionBinding
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.GetAppInfo

class OverlayPermissionDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogOverlayPermissionBinding
    var permissionsRequired = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogOverlayPermissionBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        dialog.window?.attributes = wlp
        wlp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.Companion.iranSansMediumTF)

        binding.btnGoToSetting.setOnClickListener {
            val REQUEST_CODE = 107
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.data = Uri.parse("package:" + MyApplication.currentActivity.packageName)
            MyApplication.currentActivity.startActivityForResult(myIntent, REQUEST_CODE)
            dialog.dismiss()
        }

        binding.btnDismiss.setOnClickListener {
            GetAppInfo().callAppInfoAPI()
            dialog.dismiss()
        }

        dialog.show()
    }
}