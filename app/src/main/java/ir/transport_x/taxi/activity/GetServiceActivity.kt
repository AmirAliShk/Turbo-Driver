package ir.transport_x.taxi.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ActivityGetServiceBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.AcceptService

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
            window.navigationBarColor = resources.getColor(R.color.grayLighter)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            WindowInsetsControllerCompat(
                window,
                MainActivity.binding.root
            ).isAppearanceLightStatusBars = false
            WindowInsetsControllerCompat(
                window,
                MainActivity.binding.root
            ).isAppearanceLightNavigationBars =
                true
        }
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        MyApplication.handler.postDelayed({
            val serviceId = intent.extras!!.getString("ServiceID")
            val originAddress = intent.extras!!.getString("originAddress")
            val destinationAddress = intent.extras!!.getString("destinationAddress")
            val price = intent.extras!!.getString("price")
            val serviceTypeId = intent.extras!!.getInt("ServiceType")
            val carTypeId = intent.extras!!.getInt("carType")
            val inService = intent.extras!!.getBoolean("inService")
            val description = intent.extras!!.getString("description")
            binding.txtOriginAddress.text = StringHelper.toPersianDigits(originAddress)
            binding.txtPrice.text = StringHelper.toPersianDigits(
                StringHelper.setComma(price).toString() + " تومان"
            )

            if (description != null) {
                if (description.trim() == "") {
                    binding.llDescription.visibility = View.GONE
                } else {
                    binding.llDescription.visibility = View.VISIBLE
                    binding.txtDescription.text = StringHelper.toPersianDigits(description)
                }
            }

            if (inService && MyApplication.prefManager.pricing == 1) {
                binding.llServiceType.visibility = View.VISIBLE
            } else {
                binding.llServiceType.visibility = View.GONE
            }

            binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(destinationAddress)

            binding.btnClose.setOnClickListener {
                MyApplication.currentActivity.finish()
            }

            binding.btnGetService.setOnClickListener {
                GeneralDialog()
                    .message("از دریافت سرویس اطمینان دارید؟")
                    .firstButton("بله") {
                        binding.vfAcceptService.displayedChild = 1
                        AcceptService().accept(serviceId!!, serviceTypeId, carTypeId , object : AcceptService.Listener {
                            override fun onSuccess(msg: String) {
                                binding.vfAcceptService.displayedChild = 0
                                MyApplication.prefManager.isFromGetServiceActivity = true
                                val intent =
                                    Intent(MyApplication.context, SplashActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            override fun onFailure() {
                                binding.vfAcceptService.displayedChild = 0
                            }
                        })
                    }
                    .secondButton("خیر") {}
                    .show()
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