package ir.team_x.cloud_transport.taxi_driver.app

import org.json.JSONArray

class DataHolder {
    var stationArr: JSONArray? = null

    companion object {
        private val ourInstance = DataHolder()

        fun instance(): DataHolder {
            return ourInstance
        }
    }
}