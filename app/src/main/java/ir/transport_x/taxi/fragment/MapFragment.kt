package ir.transport_x.taxi.fragment

import android.content.*
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import ir.transport_x.taxi.R
import ir.transport_x.taxi.activity.MainActivity
import ir.transport_x.taxi.activity.MainActivity.Companion.openDrawer
import ir.transport_x.taxi.app.AppKeys
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentMapBinding
import ir.transport_x.taxi.dialog.AvailableServiceDialog
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.fragment.news.NewsFragment
import ir.transport_x.taxi.fragment.services.CurrentServiceFragment
import ir.transport_x.taxi.fragment.services.FreeLoadsFragment
import ir.transport_x.taxi.gps.DataGatheringService
import ir.transport_x.taxi.gps.GPSEnable
import ir.transport_x.taxi.gps.LocationAssistant
import ir.transport_x.taxi.gps.MyLocation
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.ServiceHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import ir.transport_x.taxi.webServices.GetStatus
import org.json.JSONObject
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, LocationAssistant.Listener {
    companion object {
        val TAG = MapFragment::class.java.simpleName
        lateinit var timer: Timer
        fun stopGetStatus() {
            Log.i(TAG, "stopGetStatus: ")
            try {
                if (this::timer.isInitialized)
                    timer.cancel()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    private lateinit var binding: FragmentMapBinding
    lateinit var googleMap: GoogleMap
    lateinit var locationAssistant: LocationAssistant
    var lastLocation = Location("provider")
    lateinit var myLocationMarker: Marker
    private val STATUS_PERIOD: Long = 60000
    var driverStatus = 0
    var active = false
    var register = false
    lateinit var window: Window

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtStatus, MyApplication.iranSansMediumTF)
        TypeFaceUtil.overrideFont(binding.txtLoader, MyApplication.iranSansMediumTF)
        binding.map.onCreate(savedInstanceState)
        MapsInitializer.initialize(MyApplication.context)
        binding.map.getMapAsync(this)
        binding.txtLock.isSelected = true
        locationAssistant =
            LocationAssistant(
                MyApplication.context,
                this,
                LocationAssistant.Accuracy.HIGH,
                1000,
                true
            )

        handleStatusByServer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = activity?.window!!
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.navigationBarColor = resources.getColor(R.color.colorPageBackground)
            window.statusBarColor = resources.getColor(R.color.colorPageBackground)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightNavigationBars = true
        }

        if (MyApplication.prefManager.getLockStatus() == 1) {
            binding.txtLock.visibility = View.VISIBLE
            binding.txtLock.setTextColor(resources.getColor(R.color.colorWhite))
            binding.txtLock.background = resources.getDrawable(R.color.colorRed)
            binding.txtLock.text =
                "همکار گرامی کد شما به دلیل " + MyApplication.prefManager.getLockReasons() + " قفل گردید و امکان سرويس دهي وجود ندارد."
        } else {
            binding.txtLock.visibility = View.GONE
            if (MyApplication.prefManager.isFromGetServiceActivity) {
                MyApplication.prefManager.isFromGetServiceActivity = false
                FragmentHelper.toFragment(MyApplication.currentActivity, CurrentServiceFragment())
                    .replace()
            }
        }

        if (MyApplication.prefManager.getStationRegisterStatus()) {
            startGetStatus()
        } else {
            stopGetStatus()
        }

        binding.chbMuteNotifications.isChecked = MyApplication.prefManager.muteNotifications

        binding.imgMenu.setOnClickListener {
            openDrawer()
        }

        MyApplication.handler.postDelayed({
            if (MyApplication.prefManager.getCountNotification() > 0) {
                newsNotification()
            }
        }, 2000)

        binding.llSuggestionStation.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, SuggestionStationFragment())
                .replace()
        }

        binding.llNews.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment()).replace()
        }

        binding.llFreeLoads.setOnClickListener {
            if (!MyApplication.prefManager.getDriverStatus()) {
                GeneralDialog().message("لطفا فعال شوید").secondButton("باشه") {}
                    .show()
                return@setOnClickListener
            }
            FragmentHelper.toFragment(MyApplication.currentActivity, FreeLoadsFragment())
                .replace()
        }

        binding.llGPs.setOnClickListener {
            if (!GPSEnable.isOn()) {
                turnOnGPSDialog()
                return@setOnClickListener
            }

            animateToLocation(
                MyApplication.prefManager.getLastLocation().latitude,
                MyApplication.prefManager.getLastLocation().longitude
            )
        }

        binding.swEnterExit.setOnCheckedChangeListener { _, b ->
            if (binding.swEnterExit.isPressed) {
                if (!GPSEnable.isOn()) {
                    turnOnGPSDialog()
                    binding.swEnterExit.isChecked = (!binding.swEnterExit.isChecked)
                    return@setOnCheckedChangeListener
                }
                binding.swEnterExit.isEnabled = false
                if (b) { // i start to send driver location to server every 20 sec here
                    enterExit(1)
                } else {// i stop to send location here
                    enterExit(0)
                }
            }
        }

        binding.swStationRegister.setOnCheckedChangeListener { _, b ->
            if (!b) {
                stopGetStatus()
            }
            if (binding.swStationRegister.isPressed) {
                if (!GPSEnable.isOn()) {
                    turnOnGPSDialog()
                    binding.swStationRegister.isChecked = (!binding.swStationRegister.isChecked)
                    return@setOnCheckedChangeListener
                }
                binding.swStationRegister.isEnabled = false
                binding.vfStatus.displayedChild = 1
                if (b) {
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
                    val myLocation =
                        MyLocation()
                    myLocation.getLocation(MyApplication.currentActivity, locationResult)
                } else {
                    exitStation()
                }
            }
        }

        binding.chbMuteNotifications.setOnCheckedChangeListener { buttonView, isChecked ->
            MyApplication.prefManager.muteNotifications = isChecked
            Log.i(TAG, "onCreateView:OOOOOOOOOOOOO ${MyApplication.prefManager.muteNotifications}")
        }

        binding.llMuteNotification.setOnClickListener {
            binding.chbMuteNotifications.isChecked = !MyApplication.prefManager.muteNotifications
        }

        return binding.root
    }

    private fun newsNotification() {
        binding.cRedNews.visibility = View.VISIBLE
        val anim = AnimationUtils.loadAnimation(MyApplication.context, R.anim.fade_in_out)
        anim.repeatCount = 5
        binding.cRedNews.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.cRedNews.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    var statusReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getStringExtra(AppKeys.BROADCAST_STATUS_VALUE)
            System.gc()
            MyApplication.handler.postDelayed({ setStatus(result) }, 500)
        }
    }

    fun setStatus(intent: String) {
        try {
            val response = JSONObject(intent)
            val statusObj = response.getJSONObject("status")
            active = statusObj.getBoolean("active")
            register = statusObj.getBoolean("register")
            val statusMessage = statusObj.getString("message")
            val stationObj = statusObj.getJSONObject("station")
            MyApplication.prefManager.setDriverStatus(active)
            MyApplication.prefManager.setStationRegisterStatus(register)
            handleStatusByServer()
            refreshMyLocationMarker(active,register)
            binding.txtStatus.text = statusMessage
            binding.vfStatus.displayedChild = 0
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

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.setMinZoomPreference(11.0f)

        val cameraPosition = CameraPosition.Builder()
            .target(MyApplication.prefManager.getLastLocation())
            .zoom(12f)
            .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location?) {
                if (location == null) {
                    return
                }
                try {
                    lastLocation = location

                    if (lastLocation.latitude in 20.0..40.0) {
                        animateToLocation(lastLocation.latitude, lastLocation.longitude)
                    }
                    refreshMyLocationMarker(MyApplication.prefManager.getDriverStatus(),MyApplication.prefManager.getStationRegisterStatus())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val myLocation = MyLocation()
        myLocation.getLocation(MyApplication.currentActivity, locationResult)
    }

    private fun animateToLocation(lat: Double, lon: Double) {
        MyApplication.currentActivity.runOnUiThread {
            val latLng = LatLng(lat, lon)
            val position1 = CameraPosition.builder()
                .target(latLng)
                .zoom(16f)
                .build()

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(position1),
                200,
                null
            )
        }
    }

    fun refreshMyLocationMarker(active :Boolean, register:Boolean) = try {
        MyApplication.currentActivity.runOnUiThread {
            if (::myLocationMarker.isInitialized)
                myLocationMarker.remove()

            var bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.yellow)
            if (active && register) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.green)
            } else if (active && !register) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.red)
            } else if (!active && !register) {
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.yellow)
            }
            myLocationMarker = googleMap.addMarker(
                MarkerOptions()
                    .icon(bitmapDescriptor)
                    .position(
                        LatLng(
                            MyApplication.prefManager.getLastLocation().latitude,
                            MyApplication.prefManager.getLastLocation().longitude
                        )
                    )
            )
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    private fun enterExit(status: Int) {
        binding.vfStatus.displayedChild = 1
        driverStatus = status
        RequestHelper.builder(EndPoint.ENTER_EXIT)
            .listener(enterExitCallBack)
            .addParam("status", status)
            .post()
    }

    private val enterExitCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                binding.vfStatus.displayedChild = 0
                try {
                    binding.swEnterExit.isEnabled = true
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val status = dataObj.getBoolean("result")
                        if (status) {
                            if (driverStatus == 0) {
                                binding.swStationRegister.visibility = View.INVISIBLE
                                binding.swStationRegister.isChecked = false
                                driverDisable()
                            } else {
                                binding.swStationRegister.visibility = View.VISIBLE
                                driverEnable()
                            }
                            refreshMyLocationMarker(MyApplication.prefManager.getDriverStatus(), MyApplication.prefManager.getStationRegisterStatus())
                        } else {
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                            binding.swEnterExit.isChecked = !binding.swEnterExit.isChecked
                        }
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
                        binding.swEnterExit.isChecked = !binding.swEnterExit.isChecked
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfStatus.displayedChild = 0
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
                            startGetStatus()
                            val dataObj = jsonObject.getJSONObject("data")
                            val status = dataObj.getBoolean("result")
                            if (status) {
                                register = true
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
                    binding.vfStatus.displayedChild = 0
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfStatus.displayedChild = 0
                    binding.swStationRegister.isEnabled = true
                    binding.swStationRegister.isChecked = !binding.swStationRegister.isChecked
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
                            disableStatus()
                            MyApplication.prefManager.setStationRegisterStatus(false)
                            refreshMyLocationMarker(MyApplication.prefManager.getDriverStatus(), MyApplication.prefManager.getStationRegisterStatus())
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
                binding.vfStatus.displayedChild = 0
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfStatus.displayedChild = 0
                binding.swStationRegister.isEnabled = true
                binding.swStationRegister.isChecked = !binding.swStationRegister.isChecked
            }
        }
    }

    private fun startGetStatus() {
        Log.i(TAG, "startGetStatus: ")
        stopGetStatus()
        try {
            timer = Timer()
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        MyApplication.currentActivity.runOnUiThread {
                            GetStatus().getStatus()
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

    fun driverDisable() {
        binding.swStationRegister.visibility = View.INVISIBLE
        binding.swEnterExit.isChecked = false
        active = false
        register = false
        binding.txtStatus.text = "لطفا فعال شوید"
        stopService()
        stopGetStatus()
        MyApplication.prefManager.setDriverStatus(false)
        MyApplication.prefManager.setStationRegisterStatus(false)
    }

    fun driverEnable() {
        binding.swStationRegister.isChecked = MyApplication.prefManager.getStationRegisterStatus()
        binding.swStationRegister.visibility = View.VISIBLE
        binding.swEnterExit.isChecked = true
        binding.txtStatus.text = "در محدوده ای ثبت نیستید"
        startService()
        MyApplication.prefManager.setDriverStatus(true)
    }

    fun disableStatus() {
        binding.swStationRegister.isChecked = false
        binding.txtStatus.text = "در محدوده ای ثبت نیستید"
        stopGetStatus()
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
                driverEnable()
        } else {
                driverDisable()
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
            .show()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
        AvailableServiceDialog.dismiss()
        stopGetStatus()
        if (statusReceiver != null) LocalBroadcastManager.getInstance(MyApplication.currentActivity)
            .unregisterReceiver(statusReceiver)
        if (window != null) {
            window.statusBarColor = resources.getColor(R.color.colorPageBackground)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightNavigationBars = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationAssistant.stop()
        binding.map.onDestroy()
        AvailableServiceDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
        locationAssistant.start()
        if (statusReceiver != null) LocalBroadcastManager.getInstance(MyApplication.currentActivity)
            .registerReceiver(
                statusReceiver, IntentFilter(AppKeys.BROADCAST_STATUS_KEY)
            )

        if (MyApplication.prefManager.getStationRegisterStatus()) {
            startGetStatus()
        } else {
            stopGetStatus()
        }

        if (!isDriverActive()) {
            binding.txtStatus.text = "لطفا فعال شوید"
            binding.swStationRegister.visibility = View.INVISIBLE
            binding.swEnterExit.isChecked = false
        }
        if (!GPSEnable.isOn()) {
            turnOnGPSDialog()
        }
        window = activity?.window!!
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.navigationBarColor = resources.getColor(R.color.colorPageBackground)
        window.statusBarColor = resources.getColor(R.color.colorPageBackground)
        WindowInsetsControllerCompat(
            window,
            MainActivity.binding.root
        ).isAppearanceLightStatusBars = true
        WindowInsetsControllerCompat(
            window,
            MainActivity.binding.root
        ).isAppearanceLightNavigationBars = true
    }

    override fun onNeedLocationPermission() {}

    override fun onExplainLocationPermission() {}

    override fun onLocationPermissionPermanentlyDeclined(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onNeedLocationSettingsChange() {}

    override fun onFallBackToSystemSettings(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onNewLocationAvailable(location: Location?) {
        this.lastLocation = location!!
        MyApplication.prefManager.setLastLocation(LatLng(location.latitude, location.longitude))
    }

    override fun onMockLocationsDetected(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
    }

    override fun onError(type: LocationAssistant.ErrorType?, message: String?) {}


}