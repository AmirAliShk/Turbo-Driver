package ir.team_x.ariana.driver.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.model.LatLng
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMainBinding
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.fragment.*
import ir.team_x.ariana.driver.gps.DataGatheringService
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.*
import ir.team_x.ariana.driver.webServices.UpdateCharge
import ir.team_x.ariana.operator.utils.TypeFaceUtil
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var lastLocation = LatLng(0.0, 0.0)
    private var timer = Timer()
    private val STATUS_PERIOD: Long = 20000
    var driverStatus = 0
    var active = false
    var register = false

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
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.txtCharge, MyApplication.iranSansMediumTF)
        TypeFaceUtilJava.overrideFonts(binding.txtDriverName, MyApplication.iranSansMediumTF)
        TypeFaceUtilJava.overrideFonts(binding.txtStatus, MyApplication.iranSansMediumTF)

        MyApplication.prefManager.setAvaPID(10)//TODO move to splash response
        MyApplication.prefManager.setAvaToken("arianaDriverAABMohsenX")  // TODO change value

        if (!isDriverActive()) {
            binding.txtStatus.text = "برای وارد شدن فعال را بزنید"
            binding.swStationRegister.visibility = View.INVISIBLE
            binding.swEnterExit.isChecked = false
        }

        handleStatusByServer()

        UpdateCharge().update(object : UpdateCharge.ChargeListener {
            override fun getCharge(charge: String) {
                if (charge.isNotEmpty()) {
                    binding.vfCharge.displayedChild = 1
                    binding.txtCharge.text =
                        StringHelper.toPersianDigits(StringHelper.setComma(charge))
                } else {
                    binding.vfCharge.displayedChild = 0
                }
            }
        })

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

        binding.imgAnnouncement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ServiceHistoryFragment())
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
                binding.swStationRegister.isChecked = false
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

        binding.llAccount.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ProfileFragment())
                .replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llServiceHistory.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ServiceHistoryFragment())
                .replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llNews.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment()).replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llChat.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ChatFragment()).replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, SupportFragment()).replace()
            binding.drawerLayout.closeDrawers()
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
                        val dataObj = jsonObject.getJSONObject("data")
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            if (driverStatus == 0) {
                                driverDisable()
                            } else {
                                driverEnable()
                            }
                            getStatus()
                        } else {
                            binding.swEnterExit.isChecked = !binding.swEnterExit.isChecked
                        }
                    } else {
                        binding.swEnterExit.isChecked = !binding.swEnterExit.isChecked
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
                            val dataObj = jsonObject.getJSONObject("data")
                            val status = dataObj.getBoolean("result")
                            if (status) {
                                MyApplication.prefManager.setStationRegisterStatus(true)
                            } else {
                                binding.swStationRegister.isChecked =
                                    !binding.swStationRegister.isChecked
                            }
                        } else {
                            binding.swStationRegister.isChecked =
                                !binding.swStationRegister.isChecked
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
                        val dataObj = jsonObject.getJSONObject("data")
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            getStatus()
                            MyApplication.prefManager.setStationRegisterStatus(false)
                        } else {
                            binding.swStationRegister.isChecked =
                                !binding.swStationRegister.isChecked
                        }
                    } else {
                        binding.swStationRegister.isChecked = !binding.swStationRegister.isChecked
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
            runOnUiThread(Runnable {
                getStatus()
            })
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
                        val dataObj = jsonObject.getJSONObject("data")
                        val statusObj = dataObj.getJSONObject("status")
                        val active = statusObj.getBoolean("active")
                        val register = statusObj.getBoolean("register")
                        val statusMessage = statusObj.getString("message")
                        val stationObj = statusObj.getJSONObject("station")
                        MyApplication.prefManager.setDriverStatus(active)
                        handleStatusByServer()
                        binding.txtStatus.text = statusMessage
                        if (active && register) {
                            val distance = stationObj.getInt("distance")
                            val lat = stationObj.getDouble("lat")
                            val lng = stationObj.getDouble("lng")
                            val code = stationObj.getInt("code")
                            val borderLimit = stationObj.getInt("borderLimit")
                            binding.swEnterExit.isChecked = true
                            binding.swStationRegister.isChecked = true
                            binding.swStationRegister.visibility = View.VISIBLE
                        } else if (active && !register) {
                            binding.swStationRegister.isChecked = false
                        } else if (!active && !register) {
                            binding.swEnterExit.isChecked = false
                            binding.swStationRegister.isChecked = false
                            binding.swStationRegister.visibility = View.INVISIBLE
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
        active = false
        register = false
        binding.txtStatus.setText(R.string.update_driver_status)
        stopService()
        MyApplication.prefManager.setDriverStatus(false)
        MyApplication.prefManager.setStationRegisterStatus(false)
    }

    fun driverEnable() {
        binding.swStationRegister.isChecked = MyApplication.prefManager.getStationRegisterStatus()
        binding.swStationRegister.visibility = View.VISIBLE
        binding.swEnterExit.isChecked = true
        binding.txtStatus.setText(R.string.update_driver_status)
        startService()
        MyApplication.prefManager.setDriverStatus(true)
    }

    private fun startService() {
        ServiceHelper.start(MyApplication.currentActivity, DataGatheringService::class.java)
    }

    private fun stopService() {
        ServiceHelper.stop(MyApplication.currentActivity, DataGatheringService::class.java)
    }

    private fun isDriverActive(): Boolean {
        return ServiceHelper.isRunning(
            MyApplication.currentActivity,
            DataGatheringService::class.java
        )
    }

    private fun handleStatusByServer() {
        if (MyApplication.prefManager.getDriverStatus()) {
            if (!isDriverActive())
                driverEnable()
        } else {
            if (isDriverActive())
                driverDisable()
        }
    }

    private fun setStatusText(statusText: String) {
        binding.txtStatus.text = statusText
    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        MyApplication.prefManager.setAppRun(true)
        binding.txtCharge.text = MyApplication.prefManager.getCharge()
        startGetStatus()
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
        startGetStatus()
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.setAppRun(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGetStatus()
    }

    override fun onBackPressed() {
        KeyBoardHelper.hideKeyboard()
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            GeneralDialog()
                .message("آیا از خروج خود اطمینان دارید؟")
                .firstButton("بله") {
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }

}