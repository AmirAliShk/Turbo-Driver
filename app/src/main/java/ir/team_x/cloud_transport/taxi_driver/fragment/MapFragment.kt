package ir.team_x.cloud_transport.taxi_driver.fragment

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.activity.MainActivity
import ir.team_x.cloud_transport.taxi_driver.activity.MainActivity.Companion.openDrawer
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentChatBinding
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentMapBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.financial.FinancialFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.CurrentServiceFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.FreeLoadsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.ServiceHistoryFragment
import ir.team_x.cloud_transport.taxi_driver.gps.DataGatheringService
import ir.team_x.cloud_transport.taxi_driver.gps.GPSEnable
import ir.team_x.cloud_transport.taxi_driver.gps.MyLocation
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.ServiceHelper
import org.json.JSONObject
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    lateinit var googleMap: GoogleMap
    var lastLocation = Location("provider")
    var myLocationMarker: Marker? = null
    private lateinit var timer: Timer
    private val STATUS_PERIOD: Long = 20000
    var driverStatus = 0
    var active = false
    var register = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.map.onCreate(savedInstanceState)
        MapsInitializer.initialize(MyApplication.context)
        binding.map.getMapAsync(this)

        binding.imgMenu.setOnClickListener {
            openDrawer()
        }

        binding.llServiceManagement.setOnClickListener {
//            if (!MyApplication.prefManager.getDriverStatus()) {
//                GeneralDialog().message("لطفا فعال شوید").title("هشدار").firstButton("باشه") {}
//                    .show()
//                return@setOnClickListener
//            }
            FragmentHelper.toFragment(MyApplication.currentActivity, CurrentServiceFragment())
                .replace()
        }

        binding.llNews.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment())
                .replace()
        }

        binding.llFreeLoads.setOnClickListener {
//            if (!MyApplication.prefManager.getDriverStatus()) {
//                GeneralDialog().message("لطفا فعال شوید").title("هشدار").firstButton("باشه") {}
//                    .show()
//                return@setOnClickListener
//            }
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

        return binding.root
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
//                        refreshMyLocationMarker()
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
                .zoom(14f)
                .build()

            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(position1),
                200,
                null
            )

            refreshLocation()
        }
    }

    private fun refreshLocation() {
        myLocationMarker?.remove()
        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.taxi)
        myLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .icon(bitmapDescriptor)
                .rotation(lastLocation.bearing)
//                .title(messageMyLocationMarker)
                .position(LatLng(lastLocation.latitude, lastLocation.longitude))
        )
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

    private fun startGetStatus() {
        try {
            timer = Timer()
            timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        MyApplication.currentActivity.runOnUiThread {
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

    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            MyApplication.currentActivity.runOnUiThread(Runnable {
                getStatus()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopGetStatus()
        binding.map.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }


}