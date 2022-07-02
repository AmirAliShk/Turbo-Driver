package ir.transport_x.taxi.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class SuggestStationModel : Serializable {
    var stationCode = 0
    var countService = 0
    var position: LatLng? = null
    var fromTime: String? = null
    var stationName: String? = null
    var toTime: String? = null
    var distance = 0
    var reachedTime = 0
    var estimatedService = 0
    var registerCarCount = 0
    var passengerCount = 0
    var stationRadius = 0
    var updateTime: String? = null
    var estimatedTime = 0
    var medal: Medal? = null

    constructor(model: SuggestStationModel) {
        stationCode = model.stationCode
        countService = model.countService
        position = model.position
        fromTime = model.fromTime
        stationName = model.stationName
        toTime = model.toTime
        distance = model.distance
        reachedTime = model.reachedTime
        estimatedService = model.estimatedService
        registerCarCount = model.registerCarCount
        passengerCount = model.passengerCount
        stationRadius = model.stationRadius
        updateTime = model.updateTime
        estimatedTime = model.estimatedTime
        medal = model.medal
    }

    constructor()
}