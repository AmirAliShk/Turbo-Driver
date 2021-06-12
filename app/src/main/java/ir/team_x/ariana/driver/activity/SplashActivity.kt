package ir.team_x.ariana.driver.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.Constant
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivitySplashBinding
import ir.team_x.ariana.driver.utils.AppVersionHelper
import ir.team_x.ariana.driver.webServices.GetAppInfo
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class SplashActivity : AppCompatActivity() {
    private val permission = arrayOf(Manifest.permission.RECORD_AUDIO)

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

        binding.txtAppVersion.text = AppVersionHelper(MyApplication.context).versionName

        MyApplication.handler.postDelayed(Runnable {
            checkPermission()
        }, 1500)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val hasAudioPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            if (hasAudioPermission != PackageManager.PERMISSION_GRANTED) {
                //TODO show dialog later
                ActivityCompat.requestPermissions(
                    this,
                    permission,
                    Constant.LINPHONE_PERMISSION_REQ_CODE
                )
            } else {
                GetAppInfo().callAppInfoAPI()
            }
        }else{
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


    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0 || supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}