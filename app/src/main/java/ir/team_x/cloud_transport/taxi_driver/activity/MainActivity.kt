package ir.team_x.cloud_transport.taxi_driver.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ActivityMainBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.ProfileFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.SupportFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.financial.FinancialFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsDetailsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.CurrentServiceFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.FreeLoadsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.ServiceHistoryFragment
import ir.team_x.cloud_transport.taxi_driver.gps.DataGatheringService
import ir.team_x.cloud_transport.taxi_driver.gps.GPSEnable
import ir.team_x.cloud_transport.taxi_driver.gps.LocationAssistant
import ir.team_x.cloud_transport.taxi_driver.gps.MyLocation
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.utils.*
import ir.team_x.cloud_transport.taxi_driver.webServices.UpdateCharge
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), NewsDetailsFragment.RefreshNotificationCount,
    LocationAssistant.Listener {

    lateinit var binding: ActivityMainBinding
    lateinit var locationAssistant: LocationAssistant
    private lateinit var lastLocation: Location
    private lateinit var timer: Timer
    private val STATUS_PERIOD: Long = 20000
    var driverStatus = 0
    var active = false
    var register = false

    @SuppressLint("MissingPermission")
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
        locationAssistant = LocationAssistant(
            MyApplication.context,
            this,
            LocationAssistant.Accuracy.HIGH,
            100,
            true
        )

        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.txtCharge, MyApplication.iranSansMediumTF)
        TypeFaceUtilJava.overrideFonts(binding.txtDriverName, MyApplication.iranSansMediumTF)
        TypeFaceUtilJava.overrideFonts(binding.txtStatus, MyApplication.iranSansMediumTF)

        if (!isDriverActive()) {
            binding.txtStatus.text = "برای وارد شدن فعال را بزنید"
            binding.swStationRegister.visibility = View.INVISIBLE
            binding.swEnterExit.isChecked = false
        }

        binding.txtLock.isSelected = true
        binding.txtDriverName.text = MyApplication.prefManager.getUserName()
        binding.txtAppVersion.text = AppVersionHelper(MyApplication.context).versionName

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

        if (MyApplication.prefManager.getLockStatus() == 1) {
            binding.txtLock.visibility = View.VISIBLE
            binding.txtLock.setTextColor(resources.getColor(R.color.colorWhite))
            binding.txtLock.background = resources.getDrawable(R.color.colorRed)
            binding.txtLock.text =
                "همکار گرامی کد شما به دلیل " + MyApplication.prefManager.getLockReasons() + " قفل گردید و امکان سرويس دهي به شما وجود ندارد."
        } else {
            binding.txtLock.visibility = View.INVISIBLE
        }

        binding.imgMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

        binding.llMap.setOnClickListener {
            startActivity(Intent(MyApplication.currentActivity, MapActivity::class.java))
        }

        binding.llServiceManagement.setOnClickListener {
            if (!MyApplication.prefManager.getDriverStatus()) {
                GeneralDialog().message("لطفا فعال شوید").title("هشدار").firstButton("باشه") {}
                    .show()
                return@setOnClickListener
            }
            FragmentHelper.toFragment(MyApplication.currentActivity, CurrentServiceFragment())
                .replace()
        }

        binding.imgAnnouncement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment())
                .replace()
        }

        binding.llFreeLoads.setOnClickListener {
            if (!MyApplication.prefManager.getDriverStatus()) {
                GeneralDialog().message("لطفا فعال شوید").title("هشدار").firstButton("باشه") {}
                    .show()
                return@setOnClickListener
            }
            FragmentHelper.toFragment(MyApplication.currentActivity, FreeLoadsFragment())
                .replace()
        }

        binding.llFinancial.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, FinancialFragment()).replace()
        }

        binding.swEnterExit.setOnCheckedChangeListener { compoundButton, b ->
            if (!GPSEnable.isOn()) {
                turnOnGPSDialog()
                binding.swEnterExit.isChecked = (!binding.swEnterExit.isChecked)
                return@setOnCheckedChangeListener
            }
            binding.swEnterExit.isEnabled = false
            if (b) { // i start to send driver location to server every 20 sec here
                binding.swStationRegister.visibility = View.VISIBLE
                enterExit(1)
            } else {// i stop to send location here
                binding.swStationRegister.visibility = View.INVISIBLE
                binding.swStationRegister.isChecked = false
                enterExit(0)
            }
        }

        binding.swStationRegister.setOnCheckedChangeListener { compoundButton, b ->
            if (!GPSEnable.isOn()) {
                turnOnGPSDialog()
                binding.swStationRegister.isChecked = (!binding.swStationRegister.isChecked)
                return@setOnCheckedChangeListener
            }
            binding.swStationRegister.isEnabled = false
            if (b) {
//                MyApplication.handler.postDelayed({
                val locationResult: MyLocation.LocationResult =
                    object : MyLocation.LocationResult() {
                        override fun gotLocation(location: Location) {
                            try {
                                if ((location.latitude == 0.0) || (location.longitude == 0.0)) {
                                    if ((lastLocation.latitude == 0.0) || (lastLocation.longitude == 0.0)) {
                                        MyApplication.Toast(
                                            "درحال دریافت موقعیت لطفا بعد از چند ثانیه مجدد امتحان کنید",
                                            Toast.LENGTH_SHORT
                                        )
                                    } else {
                                        stationRegister(lastLocation)
                                    }
                                } else {
                                    stationRegister(location)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                val myLocation = MyLocation()
                myLocation.getLocation(MyApplication.currentActivity, locationResult)
//                }, 300)
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

        binding.llSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, SupportFragment()).replace()
            binding.drawerLayout.closeDrawers()
        }

    }

    private fun turnOnGPSDialog() {
        GeneralDialog()
            .message("لطفا موقعیت مکانی خود را روشن نمایید")
            .firstButton("فعال سازی") {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .secondButton("انصراف") {}
            .cancelable(false)
            .show()
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
                    binding.swEnterExit.isEnabled = true
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
                binding.swEnterExit.isEnabled = true
            }
        }
    }

    private fun stationRegister(location: Location) {
        RequestHelper.builder(EndPoint.REGISTER)
            .listener(stationRegisterCallBack)
            .addParam("lat", location.latitude)
            .addParam("lng", location.longitude)
            .post()
    }

    private val stationRegisterCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.swStationRegister.isEnabled = true
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
                    binding.swStationRegister.isEnabled = true
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
                    binding.swStationRegister.isEnabled = true
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
                binding.swStationRegister.isEnabled = true
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

    private fun startGetStatus() {
        try {
            timer = Timer()
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            getStatus()
                        }
                    }
                },
                1000,
                STATUS_PERIOD
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun stopGetStatus() {
        try {
            timer.cancel()
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
                        MyApplication.prefManager.setStationRegisterStatus(register)
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
                            binding.swEnterExit.isChecked = true
                            binding.swStationRegister.isChecked = false
                            binding.swStationRegister.visibility = View.VISIBLE
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ServiceHelper.startForeground(MyApplication.context, DataGatheringService::class.java)
        } else {
            ServiceHelper.start(MyApplication.context, DataGatheringService::class.java)
        }
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

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        KeyBoardHelper.hideKeyboard()
        MyApplication.prefManager.setAppRun(true)
        locationAssistant.start()
        if (MyApplication.prefManager.getCharge() != "")
            binding.txtCharge.text =
                StringHelper.toPersianDigits(StringHelper.setComma(MyApplication.prefManager.getCharge()))
        setBadge()
        startGetStatus()
        if (!GPSEnable.isOn()) {
            turnOnGPSDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.setAppRun(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGetStatus()
        locationAssistant.stop()
    }

    override fun onBackPressed() {
        binding.txtCharge.text =
            StringHelper.toPersianDigits(StringHelper.setComma(MyApplication.prefManager.getCharge()))
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

    fun setBadge() {
        if (MyApplication.prefManager.getCountNotification() == 0) {
            binding.txtBadgeCount.visibility = View.GONE
        } else {
            binding.txtBadgeCount.visibility = View.VISIBLE
            binding.txtBadgeCount.text = StringHelper.toPersianDigits(
                MyApplication.prefManager.getCountNotification().toString()
            )
        }
    }

    override fun refreshNotification() {
        setBadge()
    }

    override fun onNeedLocationPermission() {
    }

    override fun onExplainLocationPermission() {}

    override fun onLocationPermissionPermanentlyDeclined(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onNeedLocationSettingsChange() {
    }

    override fun onFallBackToSystemSettings(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onNewLocationAvailable(location: Location?) {
        if (location != null) {
            this.lastLocation = location
        }
    }

    override fun onMockLocationsDetected(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onError(type: LocationAssistant.ErrorType?, message: String?) {}
}