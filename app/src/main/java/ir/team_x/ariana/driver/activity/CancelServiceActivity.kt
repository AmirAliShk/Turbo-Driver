package ir.team_x.ariana.driver.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityCancelServiceBinding
import ir.team_x.ariana.driver.utils.SoundHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.driver.utils.VibratorHelper

class CancelServiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityCancelServiceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        MyApplication.handler.postDelayed({
            SoundHelper.ringing(Uri.parse("android.resource://ir.team_x.ariana.driver/" + R.raw.alarm))
            VibratorHelper.setVibrator(MyApplication.context)
        }, 500)

        val getIntentExtra = intent
        if (getIntentExtra != null) {
            val message = getIntentExtra.extras!!.getString("cancelMessage", "سرویس شما کنسل گردید")
            binding.txtDescription.text = message
        }

//        val intent = Intent(MyApplication.currentActivity, ActivitySplash::class.java)
//        startActivity(intent)
//        MyApplication.currentActivity.finish()
    }
}