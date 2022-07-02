package ir.transport_x.taxi.fragment

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import ir.transport_x.taxi.app.MyApplication
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.MapView
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import ir.transport_x.taxi.utils.TypeFaceUtil
import com.google.android.gms.maps.MapsInitializer
import java.lang.Exception
import ir.transport_x.taxi.push.AvaCrashReporter
import android.util.Log
import ir.transport_x.taxi.R
import com.google.android.gms.maps.model.CameraPosition
import ir.transport_x.taxi.gps.MyLocation
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import ir.transport_x.taxi.model.SuggestStationModel
import com.google.android.gms.maps.model.CircleOptions
import android.graphics.Color
import ir.transport_x.taxi.utils.WriteTextOnDrawable
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.transport_x.taxi.gps.DataGatheringService
import com.google.android.gms.maps.model.LatLngBounds
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.app.DataHolder
import ir.transport_x.taxi.databinding.FragmentShowRouteToStationBinding
import kotlin.math.*

class ShowRouteTOStationFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding: FragmentShowRouteToStationBinding
    var map: GoogleMap? = null
    var lastLocation = MyApplication.prefManager.getLastLocation()
    var bearing = 0f
    var myLocationMarker: Marker? = null
    private var mapFragment: MapView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            binding = FragmentShowRouteToStationBinding.inflate(inflater, container, false)
            TypeFaceUtil.overrideFont(binding.root)
            System.gc()
            mapFragment = binding.mainMap
            mapFragment!!.onCreate(savedInstanceState)
            try {
                MapsInitializer.initialize(requireActivity().applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
                AvaCrashReporter.send(e, "$TAG ,MapsInitializer")
            }
            mapFragment!!.getMapAsync(this)
            binding.imgMyLocation.setOnClickListener {
                val lat = lastLocation.latitude
                val lon = lastLocation.longitude
                if (lat in 20.0..40.0) {
                    animateToLocation(lat, lon)
                }
            }
            binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "onCreateView: " + e.message)
            AvaCrashReporter.send(e, "$TAG ,onCreateView")
        }
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isMapToolbarEnabled = false
        map!!.uiSettings.isZoomControlsEnabled = false
        map!!.uiSettings.isRotateGesturesEnabled = false
//        map!!.isTrafficEnabled = MyApplication.prefManager.isTraffic()//todo
        map!!.isMyLocationEnabled = false
        refreshMyLocationMarker()

//        if (MyApplication.prefManager.isDarkMode()) {//todo
//            try {
//                val success = map!!.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                        MyApplication.currentActivity,
//                        R.raw.style_map_gray
//                    )
//                )
//                if (!success) {
////          MyApplication.ErrorToast("خطاااااا",0);
//                    Log.e(TAG, "Style parsing failed.")
//                }
//            } catch (e: NotFoundException) {
//                e.printStackTrace()
//                AvaCrashReporter.send(e, "$TAG ,setMapStyle")
//                Log.e(TAG, "Can't find style. Error: ", e)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                AvaCrashReporter.send(e, "$TAG ,setMapStyle1")
//            }
//        }

        val cameraPosition = CameraPosition.Builder()
            .target(MyApplication.prefManager.getLastLocation())
            .zoom(12f)
            .build()
        val locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
            override fun gotLocation(location: Location) {
                if (location == null) {
                    return
                }
                try {
                    lastLocation = LatLng(location.latitude, location.longitude)
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG ,locationResult")
                }
            }
        }
        val myLocation = MyLocation()
        myLocation.getLocation(MyApplication.currentActivity, locationResult)
        map!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val lat = lastLocation.latitude
        val lon = lastLocation.longitude
        if (lat in 20.0..40.0) {
            animateToLocation(lat, lon)
        }
        val suggest = SuggestStationModel(DataHolder.instance().suggest)
        val stationPosition = suggest.position
        val stationCode = suggest.stationCode
        val stationBorder = suggest.stationRadius
        val stationName = suggest.stationName
        val circleOptions = CircleOptions()
            .center(stationPosition) //set center
            .radius(stationBorder.toDouble()) //set radius in meters
            .fillColor(Color.parseColor("#110000ff")) //default
            .strokeColor(Color.BLUE)
            .strokeWidth(5f)
        binding.txtDesc.text =
            "پیش بینی ما دریافت " + suggest.estimatedService + "مسافر برای دو ساعت آینده در این محدوده و حداکثر زمان انتظار برای دریافت سفر " + suggest.estimatedTime + " دقیقه میباشد."
        binding.txtStationName.text = stationName

//    PolylineOptions polylineOptions = new PolylineOptions()
//            .add(MyApplication.prefManager.getLastLocation())
//            .add(stationPosition)
//            .color(Color.parseColor("#263238"))
//            .width(5);
//    map.addPolyline(polylineOptions);
//
//    PolylineOptions polylineOptions2 = new PolylineOptions()
//            .add(MyApplication.prefManager.getLastLocation())
//            .add(stationPosition)
//            .color(Color.parseColor("#263238"))
//            .width(5);
//    map.addPolyline(polylineOptions2);

        if (circleOptions != null) map!!.addCircle(circleOptions)
        val id: Int
//        if (MyApplication.prefManager.isDarkMode()) {//todo
//            id = R.mipmap.circle_dark
//        } else {
        id = R.mipmap.circle
//        }
        val bmp = WriteTextOnDrawable.write(id, stationCode.toString() + "", 18, 2)
        map!!.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                .position(stationPosition)
        )

        tryShowAllMarkerInPage(
            stationPosition!!,
            MyApplication.prefManager.getLastLocation(),
            getDestinationPoint(stationPosition, 0.0, (stationBorder / 1000).toDouble())!!,
            getDestinationPoint(stationPosition, 90.0, (stationBorder / 1000).toDouble())!!,
            getDestinationPoint(stationPosition, 180.0, (stationBorder / 1000).toDouble())!!,
            getDestinationPoint(stationPosition, 270.0, (stationBorder / 1000).toDouble())!!
        )
    }

    private fun animateToLocation(latitude: Double, longtitude: Double) {
        Log.i(TAG, "animateToLocation: lat:$latitude lon:$longtitude")
        val latlng = LatLng(latitude, longtitude)
        val position1 = CameraPosition.builder()
            .target(latlng)
            .zoom(16f)
            .build()
        if (map != null) map!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(position1),
            100,
            null
        )
    }

    var receiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val lat = intent.getStringExtra(DataGatheringService.GDS_LAT)
            val lon = intent.getStringExtra(DataGatheringService.GDS_LON)
            val speed = intent.getStringExtra(DataGatheringService.GDS_SPEED)
            val b = intent.getStringExtra(DataGatheringService.GDS_BEARING)
            val latD = lat.toDouble()
            val lonD = lon.toDouble()
            bearing = b.toFloat()
            lastLocation = LatLng(latD, lonD)
        }
    }

    private fun refreshMyLocationMarker() {
        try {
            if (lastLocation.latitude >= 25 && lastLocation.longitude <= 62) {
                try {
                    if (myLocationMarker != null) {
                        myLocationMarker!!.remove()
                        myLocationMarker = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG ,myLocationMarker.remove")
                }
                if (map != null) myLocationMarker = map!!.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.eco_car))
                        .rotation(bearing)
                        .position(lastLocation)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG ,refreshMyLocationMarker")
            Log.e(TAG, "addMyLocationMarker: " + e.message)
        }
    }

    private fun tryShowAllMarkerInPage(vararg latLng: LatLng) {
        try {
            if (latLng.isEmpty()) return
            var startPoint: LatLng
            var endPoint: LatLng
            startPoint = latLng[0]
            endPoint = latLng[0]
            map!!.setPadding(150, binding.llTopInfo.measuredHeight + 150, 150, 150)
            for (location in latLng) {
                if (location.latitude < startPoint.latitude) startPoint =
                    LatLng(location.latitude, startPoint.longitude)
                if (location.latitude > endPoint.latitude) endPoint =
                    LatLng(location.latitude, endPoint.longitude)
                if (location.longitude > endPoint.longitude) endPoint =
                    LatLng(endPoint.latitude, location.longitude)
                if (location.longitude < startPoint.longitude) startPoint =
                    LatLng(startPoint.latitude, location.longitude)
            }
            val latlngBounds = createLatLngBoundsObject(startPoint, endPoint)
            map!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 90))
            MyApplication.handler.postDelayed(
                { if (map != null) map!!.setPadding(0, 0, 0, 0) },
                500
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG ,tryShowAllMarkerInPage")
        }
    }

    private fun getDestinationPoint(source: LatLng?, brng: Double, dist: Double): LatLng? {
        var brng = brng
        var dist = dist
        dist /= 6371
        brng = Math.toRadians(brng)
        val lat1 = Math.toRadians(source!!.latitude)
        val lon1 = Math.toRadians(source.longitude)
        val lat2 = asin(
            sin(lat1) * cos(dist) +
                    cos(lat1) * sin(dist) * cos(brng)
        )
        val lon2 = lon1 + atan2(
            sin(brng) * sin(dist) *
                    cos(lat1),
            cos(dist) - sin(lat1) *
                    sin(lat2)
        )
        return if (java.lang.Double.isNaN(lat2) || java.lang.Double.isNaN(lon2)) {
            null
        } else LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }

    override fun onPause() {
        super.onPause()
        // unregisterReceiver the broadcast send from DataGatheringService
        if (receiver != null) LocalBroadcastManager.getInstance(MyApplication.currentActivity)
            .unregisterReceiver(
                receiver!!
            )
        if (mapFragment != null) mapFragment!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (receiver != null) LocalBroadcastManager.getInstance(MyApplication.currentActivity)
            .registerReceiver(
                receiver!!, IntentFilter(DataGatheringService.GDS_RESULT)
            )
        if (mapFragment != null) mapFragment!!.onResume()
    }

    override fun onDestroyView() {
        try {
            // Remove the map Fragment from the view
            if (mapFragment != null) {
                try {
                    mapFragment!!.onDestroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG ,onDestroyView1")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG ,onDestroyView")
        }
        super.onDestroyView()
    }

    companion object {
        val TAG = ShowRouteTOStationFragment::class.java.simpleName
        fun distFrom(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
            val earthRadius = 6371000.0 //meters
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(
                Math.toRadians(
                    lat2
                )
            ) *
                    sin(dLng / 2) * sin(dLng / 2)
            val c =
                2 * atan2(sqrt(a), sqrt(1 - a))
            return (earthRadius * c).toFloat()
        }

        fun createLatLngBoundsObject(
            firstLocation: LatLng?,
            secondLocation: LatLng?
        ): LatLngBounds? {
            if (firstLocation != null && secondLocation != null) {
                val builder = LatLngBounds.Builder()
                builder.include(firstLocation).include(secondLocation)
                return builder.build()
            }
            return null
        }
    }
}