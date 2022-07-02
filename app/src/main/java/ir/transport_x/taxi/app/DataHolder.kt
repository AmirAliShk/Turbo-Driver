package ir.transport_x.taxi.app

import ir.transport_x.taxi.model.SuggestStationModel
import org.json.JSONArray

class DataHolder {

    var stationArr: JSONArray? = null
    lateinit var suggest: SuggestStationModel

    companion object {
        private val ourInstance = DataHolder()

        fun instance(): DataHolder {
            return ourInstance
        }
    }
}