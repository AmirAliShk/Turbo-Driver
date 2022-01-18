package ir.team_x.cloud_transport.taxi_driver.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ActivityGetServiceBinding
import ir.team_x.cloud_transport.taxi_driver.utils.KeyBoardHelper
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import ir.team_x.cloud_transport.taxi_driver.webServices.AcceptService

class GetServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGetServiceBinding.inflate(layoutInflater)
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

        MyApplication.handler.postDelayed({
            val callTime = intent.extras!!.getString("CallTime")
            val serviceId = intent.extras!!.getString("ServiceID")
            val originDesc = intent.extras!!.getString("OrginDesc")
            val originAddress = intent.extras!!.getString("originAddress")
            val destinationAddress = intent.extras!!.getString("destinationAddress")
            val price = intent.extras!!.getString("price")
            val inService = intent.extras!!.getBoolean("inService")
            val carType = intent.extras!!.getString("carType")
            val cargoType = intent.extras!!.getString("cargoType")
            val description = intent.extras!!.getString("description")
            val fixesDescription = intent.extras!!.getString("fixesDescription")
            val returnBack = intent.extras!!.getString("returnBack")
            binding.txtOriginAddress.text = StringHelper.toPersianDigits(originAddress)
            binding.txtPrice.text = StringHelper.toPersianDigits(
                StringHelper.setComma(price).toString() + " تومان"
            )

            binding.btnClose.setOnClickListener {
                MyApplication.currentActivity.finish()
            }

            binding.btnGetService.setOnClickListener {
                binding.vfAcceptService.displayedChild = 1
                AcceptService().accept(serviceId!!, object : AcceptService.Listener {
                    override fun onSuccess() {
                        binding.vfAcceptService.displayedChild = 0
                        val intent = Intent(MyApplication.context, SplashActivity::class.java)
                        startActivity(intent)

                        finish()
                    }

                    override fun onFailure() {
                        binding.vfAcceptService.displayedChild = 0
                    }
                })
            }

        }, 200)

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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

}