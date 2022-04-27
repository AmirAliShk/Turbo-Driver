package ir.transport_x.taxi.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.DataHolder
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ActivitySplashBinding
import ir.transport_x.taxi.dialog.OverlayPermissionDialog
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import ir.transport_x.taxi.webServices.GetAppInfo
import org.acra.ACRA

class SplashActivity : AppCompatActivity() {
    private val permission =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.pageBackground)
            WindowInsetsControllerCompat(window, binding.root).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(window, binding.root).isAppearanceLightNavigationBars =
                true
        }
        TypeFaceUtil.overrideFont(binding.root)

        ACRA.getErrorReporter()
            .putCustomData("LineCode", MyApplication.prefManager.getDriverId().toString())
        ACRA.getErrorReporter().putCustomData("DriverName", MyApplication.prefManager.getUserName())
        ACRA.getErrorReporter()
            .putCustomData("projectId", MyApplication.prefManager.getAvaPID().toString())

//        binding.txtAppVersion.text = AppVersionHelper(MyApplication.context).versionName
        MyApplication.avaStart()
        DataHolder.instance().stationArr = null

        MyApplication.handler.postDelayed({
            checkPermission()
        }, 500)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    MyApplication.currentActivity,
                    permission,
                    100
                )
            } else if (!Settings.canDrawOverlays(MyApplication.context)) {
                OverlayPermissionDialog().show()
            } else {
                GetAppInfo().callAppInfoAPI()
            }
        } else {
            GetAppInfo().callAppInfoAPI()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 107) {
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)
                GetAppInfo().callAppInfoAPI()
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        KeyBoardHelper.hideKeyboard()
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0 || supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}