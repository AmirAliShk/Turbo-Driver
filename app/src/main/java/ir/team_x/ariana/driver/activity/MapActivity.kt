package ir.team_x.ariana.driver.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.DataHolder
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMapBinding
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.gps.LocationAssistant
import ir.team_x.ariana.driver.gps.MyLocation
import ir.team_x.ariana.driver.model.StationModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.WriteTextOnDrawable
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt

class MapActivity : AppCompatActivity(), OnMapReadyCallback, LocationAssistant.Listener {

    private lateinit var googleMap: GoogleMap
    lateinit var binding: ActivityMapBinding
    lateinit var locationAssistant: LocationAssistant
    lateinit var lastLocation: Location
    var myLocationMarker: Marker? = null
    lateinit var stationCircle: Circle
    private val markerList: ArrayList<StationModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.map.onCreate(savedInstanceState)
        MapsInitializer.initialize(MyApplication.context)
        binding.map.getMapAsync(this)

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

        binding.imgBack.setOnClickListener {
            startActivity(Intent(MyApplication.currentActivity, MainActivity::class.java))
            finish()
        }

        binding.imgGps.setOnClickListener {
            Log.i("TAG", "onCreate: $lastLocation")
            if (lastLocation.latitude == null || lastLocation.latitude == 0.0 || lastLocation.longitude == 0.0 || lastLocation.longitude == null) {
                GeneralDialog().message("متاسفانه موقعیت در دسترس نمیباشد پس از چند لحظه مجدد امتحان کنید")
                    .firstButton("باشه") {}.show()
                return@setOnClickListener
            }
            stationCircle.remove()
            animateToLocation(lastLocation.latitude, lastLocation.longitude)
        }

    }

    override fun onResume() {
        super.onResume()
        MyApplication.prefManager.setAppRun(true)
        MyApplication.currentActivity = this
        binding.map.onResume()
        locationAssistant.start()
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.setAppRun(false)
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
        locationAssistant.stop()
    }

    override fun onBackPressed() {
        startActivity(Intent(MyApplication.currentActivity, MainActivity::class.java))
        finish()
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

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

            MyApplication.handler.postDelayed({
//                if (DataHolder.instance().stationArr == null) {
                    getStation()
//                } else {
//                    DataHolder.instance().stationArr?.let { showStation(it) }
//                }
            }, 500)

            googleMap.setOnCameraChangeListener {
                try {
                    hideStation()
                    DataHolder.instance().stationArr?.let { it1 -> showStation(it1) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun animateToLocation(lat: Double, lon: Double) {
        val latlng = LatLng(lat, lon)
        val position1 = CameraPosition.builder()
            .target(latlng)
            .zoom(14f)
            .build()

        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(position1),
            200,
            null
        )

        refreshLocation()

        val circleOptions = CircleOptions()
            .center(LatLng(lastLocation.latitude, lastLocation.longitude)) //set center
            .radius(200.toDouble()) //set radius in meters
            .fillColor(MyApplication.currentActivity.resources.getColor(R.color.grayTransparent)) //default
            .strokeColor(MyApplication.currentActivity.resources.getColor(R.color.grayDark))
            .strokeWidth(5f)

        stationCircle = googleMap.addCircle(circleOptions)
    }

    private fun refreshLocation() {
        myLocationMarker?.remove()
        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.pin)
        myLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .icon(bitmapDescriptor)
                .rotation(lastLocation.bearing)
//                .title(messageMyLocationMarker)
                .position(LatLng(lastLocation.latitude, lastLocation.longitude))
        )
    }

    private fun hideStation() {
        if (markerList == null || markerList.isEmpty()) {
            return
        }
        for (i in markerList.indices) {
            val marker: Marker? = markerList[i].marker
            if (marker != null) {
                if (marker.isVisible) marker.remove()
            }
        }
    }

    private fun distFrom(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Float {
        val earthRadius = 6371000.0 //meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(
            Math.toRadians(lat2)
        ) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }

    private fun showStation(stationArr: JSONArray) {
        val mUpCameraPosition: CameraPosition = googleMap.cameraPosition
        val center = LatLng(mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)

        MyApplication.handler.post {
            for (i in 0 until stationArr.length()) {
                val dataObj = stationArr.getJSONObject(i)
                val code = dataObj.getInt("stationCode")
                val name = dataObj.getString("stationName")
                val latLng = LatLng(dataObj.getDouble("lat"), dataObj.getDouble("long"))
                val count = dataObj.getInt("countService")

//                if (distFrom(
//                        latLng.longitude,
//                        latLng.longitude,
//                        center.latitude,
//                        center.longitude
//                    ) < 3000
//                ) {
                addStationMarker(
                    latLng,
                    count.toString(),
                    code,
                    name
                )
//                }
            }
        }
    }

    private fun addStationMarker(latLng: LatLng, count: String, code: Int, name: String) {
        var bmp: Bitmap = WriteTextOnDrawable.write(R.mipmap.green_marker, count, 18, 2)
        when {
            count in 1..2 -> {
                bmp = WriteTextOnDrawable.write(R.mipmap.green_marker, count, 18, 2)
            }
            count in 3..5 -> {
                bmp = WriteTextOnDrawable.write(R.mipmap.yellow_marker, count, 18, 2)
            }
            count > 5.toString() -> {
                bmp = WriteTextOnDrawable.write(R.mipmap.red_marker, count, 18, 2)
            }
        }

        try {
            val model = StationModel(
                code,
                name,
                latLng,
                count.toInt(),
                googleMap.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .position(latLng)
                )
            )
            markerList.add(model)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getStation() {
        RequestHelper.builder(EndPoint.STATION)
            .listener(stationCallBack)
            .get()
    }

    private val stationCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {stationCode: 1,stationName: "غدیر",lat: 36.257052,long: 59.619728,countService: 7}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        DataHolder.instance().stationArr = dataArr
                        showStation(dataArr)
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

    override fun onNeedLocationPermission() {

    }

    override fun onExplainLocationPermission() {

    }

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
        this.lastLocation = location!!
    }

    override fun onMockLocationsDetected(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {

    }

    override fun onError(type: LocationAssistant.ErrorType?, message: String?) {

    }


}