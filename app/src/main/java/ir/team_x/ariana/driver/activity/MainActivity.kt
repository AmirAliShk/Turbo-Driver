package ir.team_x.ariana.driver.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.model.LatLng
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMainBinding
import ir.team_x.ariana.driver.fragment.FinancialFragment
import ir.team_x.ariana.driver.fragment.FreeLoadsFragment
import ir.team_x.ariana.driver.fragment.ManageServiceFragment
import ir.team_x.ariana.driver.fragment.NewsFragment
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var lastLocation = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        TypeFaceUtil.overrideFont(binding.root, MyApplication.iranSansMediumTF)

        MyApplication.prefManager.setAvaPID(10)
        MyApplication.prefManager.setAvaToken("")

        binding.imgMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

        binding.llMap.setOnClickListener {
            startActivity(Intent(MyApplication.currentActivity, MapActivity::class.java))
        }

        binding.llServiceManagement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ManageServiceFragment())
                .replace()
        }

        binding.llFreeLoads.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, FreeLoadsFragment()).replace()
        }

        binding.llFinancial.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, FinancialFragment()).replace()
        }

        binding.swEnterExit.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                enterExit(1)
            } else {
                enterExit(0)
            }
        }

        binding.swStationRegister.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                stationRegister()
            }else{
                exitStation()
            }
        }

        binding.imgAnnouncement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment()).replace()
        }

    }

    private fun enterExit(status: Int) {
        RequestHelper.builder(EndPoint.ENTER_EXIT)
            .listener(enterExitCallBack)
            .addParam("status", status)
            .post()
    }

    private val enterExitCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        val dataObj = dataArr.getJSONObject(0)
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
    }

    private fun stationRegister() {
        RequestHelper.builder(EndPoint.REGISTER)
            .listener(stationRegisterCallBack)
            .addParam("lat", "36.298536")
            .addParam("lng", "59.572962")
            .post()
    }

    private val stationRegisterCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        val dataObj = dataArr.getJSONObject(0)
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
    }

    private fun exitStation() {
        RequestHelper.builder(EndPoint.EXIT)
            .addParam("","")
            .listener(exitStationCallBack)
            .put()
    }

    private val exitStationCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        val dataObj = dataArr.getJSONObject(0)
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
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