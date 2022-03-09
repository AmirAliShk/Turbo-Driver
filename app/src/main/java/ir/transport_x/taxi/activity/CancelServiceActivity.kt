package ir.transport_x.taxi.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ActivityCancelServiceBinding
import ir.transport_x.taxi.utils.SoundHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.utils.VibratorHelper
import ir.transport_x.taxi.webServices.UpdateCharge

class CancelServiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityCancelServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        binding.rbContent.startRippleAnimation();

        MyApplication.handler.postDelayed({
            SoundHelper.ringing(Uri.parse(MyApplication.SOUND + R.raw.alarm))
            VibratorHelper.setVibrator(MyApplication.context)
        }, 500)

        val getIntentExtra = intent
        if (getIntentExtra != null) {
            val message = getIntentExtra.extras!!.getString("cancelMessage", "سرویس شما کنسل گردید")
            binding.txtDescription.text = message
        }

        binding.btnSubmit.setOnClickListener {
            val intent = Intent(MyApplication.context,SplashActivity::class.java)
            startActivity(intent)
            finish()
        }

        val manager = MyApplication.currentActivity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val lock = manager.newKeyguardLock("CancelServiceActivityKeyGuard")
        lock.disableKeyguard()

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
        }, 500)

    }
}