package ir.team_x.ariana.driver.app

import org.json.JSONArray

class DataHolder {
    var stationArr: JSONArray? = null



    companion object {
        private var ourInstance: DataHolder? = null
        val instance: DataHolder?
            get() {
                if (ourInstance == null) {
                    ourInstance = DataHolder()
                }
                return ourInstance
            }
    }
}