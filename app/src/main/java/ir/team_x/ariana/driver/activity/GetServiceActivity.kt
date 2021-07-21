package ir.team_x.ariana.driver.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityGetServiceBinding
import ir.team_x.ariana.driver.fragment.services.CurrentServiceFragment
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.driver.webServices.AcceptService

class GetServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGetServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
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
            binding.txtOriginAddress.text = StringHelper.toPersianDigits(originAddress)
            binding.txtDestinationAddress.text = StringHelper.toPersianDigits(destinationAddress)
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
                        FragmentHelper.toFragment(
                            MyApplication.currentActivity,
                            CurrentServiceFragment()
                        )
                            .setStatusBarColor(MyApplication.context.resources.getColor(R.color.colorBlack))
                            .replace()
                    }

                    override fun onFailure() {
                        binding.vfAcceptService.displayedChild = 0
                    }
                })
            }

        }, 100)

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
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

}