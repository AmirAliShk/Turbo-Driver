package ir.team_x.cloud_transport.taxi_driver.dialog

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
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogOverlayPermissionBinding
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import ir.team_x.cloud_transport.taxi_driver.webServices.GetAppInfo

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