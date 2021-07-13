package ir.team_x.ariana.driver.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class StationModel(
    var code:Int,
    var name:String,
    var latLng: LatLng,
    var serviceCount:Int,
    var marker: Marker
)