package ir.team_x.ariana.driver.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMapBinding
import ir.team_x.ariana.driver.gps.LocationAssistant

class MapActivity : AppCompatActivity(), OnMapReadyCallback, LocationAssistant.Listener {

    private lateinit var googleMap: GoogleMap
    lateinit var binding: ActivityMapBinding
    lateinit var locationAssistant: LocationAssistant
    lateinit var lastLocation: Location
    lateinit var myLocationMarker: Marker
    lateinit var stationCircle: Circle

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
            false
        )

        binding.imgBack.setOnClickListener {
            startActivity(Intent(MyApplication.currentActivity, MainActivity::class.java))
            finish()
        }

        binding.imgGps.setOnClickListener {
            myLocationMarker.remove()
            stationCircle.remove()
            animateToLocation(lastLocation.latitude, lastLocation.longitude)
        }

        MyApplication.handler.postDelayed({
            animateToLocation(lastLocation.latitude, lastLocation.longitude)
        }, 500)

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

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
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
        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.pin)
        myLocationMarker = googleMap.addMarker(MarkerOptions()
                .icon(bitmapDescriptor)
                .rotation(lastLocation.bearing)
//                .title(messageMyLocationMarker)
                .position(LatLng(lastLocation.latitude, lastLocation.longitude))
        )
    }

    override fun onNeedLocationPermission() {
        TODO("Not yet implemented")
    }

    override fun onExplainLocationPermission() {
        TODO("Not yet implemented")
    }

    override fun onLocationPermissionPermanentlyDeclined(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun onNeedLocationSettingsChange() {
        TODO("Not yet implemented")
    }

    override fun onFallBackToSystemSettings(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun onNewLocationAvailable(location: Location?) {
        this.lastLocation = location!!
    }

    override fun onMockLocationsDetected(
        fromView: View.OnClickListener?,
        fromDialog: DialogInterface.OnClickListener?
    ) {
        TODO("Not yet implemented")
    }

    override fun onError(type: LocationAssistant.ErrorType?, message: String?) {
        TODO("Not yet implemented")
    }


}