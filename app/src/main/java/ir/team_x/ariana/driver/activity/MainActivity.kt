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
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.fragment.FinancialFragment
import ir.team_x.ariana.driver.fragment.FreeLoadsFragment
import ir.team_x.ariana.driver.fragment.NewsFragment
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.webServices.GetDriverStatus
import ir.team_x.ariana.operator.utils.TypeFaceUtil
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var lastLocation = LatLng(0.0, 0.0)
    private var timer = Timer()
    private val STATUS_PERIOD: Long = 20000
    var driverStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        TypeFaceUtil.overrideFont(binding.root, MyApplication.iranSansMediumTF)

        MyApplication.prefManager.setAvaPID(10)//TODO move to splash response
        MyApplication.prefManager.setAvaToken("arianaDriverAABMohsenX")  // TODO change value

        if (MyApplication.prefManager.getDriverStatus()) {
            driverEnable()
        } else {
            driverDisable()
        }

        binding.imgMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

        binding.llMap.setOnClickListener {
            startActivity(Intent(MyApplication.currentActivity, MapActivity::class.java))
        }

        binding.llServiceManagement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, CurrentServiceFragment())
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
                binding.swStationRegister.visibility = View.VISIBLE
                enterExit(1)
            } else {
                binding.swStationRegister.visibility = View.INVISIBLE
                enterExit(0)
            }
        }

        binding.swStationRegister.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                stationRegister()
            } else {
                exitStation()
            }
        }

        binding.imgAnnouncement.setOnClickListener {
//            RequestHelper.builder(EndPoint.ATM) //TODO remove this in own fragment
//                .listener(ATMCallBack)
//                .addParam("driverCode", 1)
//                .addParam("cardNumber", 1)
//                .addParam("bankName", 1)
//                .addParam("trackingCode", 3322)
//                .addParam("price", "100000")
//                .addParam("description", "noori")
//                .post()
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment()).replace()
        }

    }

    private val ATMCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

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

    private fun enterExit(status: Int) {
        driverStatus = status
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
                        getStatus()
                        val dataArr = jsonObject.getJSONArray("data")
                        val dataObj = dataArr.getJSONObject(0)
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            if (driverStatus == 0) {
                                driverDisable()
                            } else {
                                driverEnable()
                            }
                            getStatus()
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

    private val stationRegisterCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val jsonObject = JSONObject(args[0].toString())
                        val success = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")

                        if (success) {
                            getStatus()
                            val dataArr = jsonObject.getJSONArray("data")
                            val dataObj = dataArr.getJSONObject(0)
                            val status = dataObj.getBoolean("result")
                            if (status) {
                                MyApplication.prefManager.setStationRegisterStatus(true)
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
            .addParam("", "")
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
                            MyApplication.prefManager.setStationRegisterStatus(false)
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

    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            getStatus()
        }
    }

    private fun startGetStatus() { //TODO where I have to call this fun? which one is better?
        try {
            timer = Timer()
            timer.scheduleAtFixedRate(
                timerTask,
                0,
                STATUS_PERIOD
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun stopGetStatus() {
        try {
            if (timer != null) {
                timerTask.cancel()
                timer.cancel()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getStatus() {
        RequestHelper.builder(EndPoint.STATUS)
            .listener(statusCallBack)
            .get()
    }

    private val statusCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد.","data":[{"active":1,"stationId":2,"distance":10,"stationName":"کلاهدوز","stationLat":36.29866,"stationLong":59.572666,"borderLimit":200}]}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        val dataObj = dataArr.getJSONObject(0)
                        val active = dataObj.getInt("active")
                        val stationId = dataObj.getInt("stationId")
                        val distance = dataObj.getInt("distance")
                        val stationName = dataObj.getString("stationName")
                        val borderLimit = dataObj.getString("borderLimit")

                        if (active == 0) {
                            binding.txtStatus.text = "لطفا فعال شوید"
                        } else if (active == 1 && stationId == 0) {
                            binding.txtStatus.text = "لطفا ثبت محدوده کنید"
                        } else {
                            binding.txtStatus.text = "شما در محدوده $stationName ثبت هستید"
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

    fun driverDisable() {
        binding.swStationRegister.visibility = View.INVISIBLE
        binding.swEnterExit.isChecked = false
        binding.txtStatus.text = "لطفا فعال شوید"
        // disable GPS service here
        MyApplication.prefManager.setDriverStatus(false)
        MyApplication.Toast("با موفقیت غیرفعال شدید", Toast.LENGTH_SHORT)
    }

    fun driverEnable() {
        binding.swStationRegister.isChecked = MyApplication.prefManager.getStationRegisterStatus()
        binding.swStationRegister.visibility = View.VISIBLE
        binding.swEnterExit.isChecked = true
        binding.txtStatus.text = "درحال بروزرسانی وضعیت"
        // enable GPS service here
        MyApplication.prefManager.setDriverStatus(true)
        MyApplication.Toast("با موفقیت فعال شدید", Toast.LENGTH_SHORT)
    }

    //    private fun exitStation() { //TODO add this
//        RequestHelper.builder(EndPoint.EXIT)
//            .addParam("","")
//            .listener(exitStationCallBack)
//            .put()
//    }
//
//    private val exitStationCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
//        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
//            MyApplication.handler.post {
//                try {
//                    val jsonObject = JSONObject(args[0].toString())
//                    val success = jsonObject.getBoolean("success")
//                    val message = jsonObject.getString("message")
//
//                    if (success) {
//                        val dataArr = jsonObject.getJSONArray("data")
//                        val dataObj = dataArr.getJSONObject(0)
//                        val status = dataObj.getBoolean("result")
//                        if (status) {
//                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
//            MyApplication.handler.post {
//
//            }
//        }
//    }
//
    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        startGetStatus()
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGetStatus()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

}