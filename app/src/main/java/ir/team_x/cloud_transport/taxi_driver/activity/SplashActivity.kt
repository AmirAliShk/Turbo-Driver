package ir.team_x.cloud_transport.taxi_driver.activity

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
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.DataHolder
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ActivitySplashBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.OverlayPermissionDialog
import ir.team_x.cloud_transport.taxi_driver.utils.KeyBoardHelper
import ir.team_x.cloud_transport.taxi_driver.webServices.GetAppInfo
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil
import org.acra.ACRA

class SplashActivity : AppCompatActivity() {
    private val permission =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.colorBlack)
            window.statusBarColor = resources.getColor(R.color.colorBlack)
        }
        TypeFaceUtil.overrideFont(binding.root)

        ACRA.getErrorReporter().putCustomData("LineCode", MyApplication.prefManager.getDriverId().toString())
        ACRA.getErrorReporter().putCustomData("DriverName", MyApplication.prefManager.getUserName())
        ACRA.getErrorReporter().putCustomData("projectId", MyApplication.prefManager.getAvaPID().toString())

//        binding.txtAppVersion.text = AppVersionHelper(MyApplication.context).versionName
        MyApplication.avaStart()
        DataHolder.instance().stationArr = null

        MyApplication.handler.postDelayed({
            checkPermission()
        }, 500)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val hasAudioPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if ((hasAudioPermission != PackageManager.PERMISSION_GRANTED)) {
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