package ir.team_x.ariana.driver.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityCancelServiceBinding
import ir.team_x.ariana.driver.utils.SoundHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.driver.utils.VibratorHelper
import ir.team_x.ariana.driver.webServices.UpdateCharge


class CancelServiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityCancelServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        binding.rbContent.startRippleAnimation();

        MyApplication.handler.postDelayed({
            SoundHelper.ringing(Uri.parse("android.resource://ir.team_x.ariana.driver/" + R.raw.alarm))
            VibratorHelper.setVibrator(MyApplication.context)
        }, 500)

        val getIntentExtra = intent
        if (getIntentExtra != null) {
            val message = getIntentExtra.extras!!.getString("cancelMessage", "سرویس شما کنسل گردید")
            binding.txtDescription.text = message
        }

        binding.btnSubmit.setOnClickListener {
            val intent = Intent(MyApplication.context, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }

        UpdateCharge().update(object : UpdateCharge.ChargeListener {
            override fun getCharge(charge: String) {
            }
        })

    }

    override fun onPause() {
        super.onPause()
        MyApplication.handler.postDelayed({
            SoundHelper.stop()
            VibratorHelper.stopVibrator()
        }, 1000)

    }
}