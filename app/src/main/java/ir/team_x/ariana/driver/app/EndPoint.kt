package ir.team_x.ariana.driver.app

import ir.team_x.ariana.driver.BuildConfig


public class EndPoint {

    companion object {

        public val IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"
        val HAWKEYE_IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"
        val PUSH_IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"

        val IP_PORT = if (BuildConfig.DEBUG) "1881" else "1881"
        val HAWKEYE_PORT = if (BuildConfig.DEBUG) "1890" else "1890"

        const val CRASH_REPORT = "http://turbotaxi.ir:6061/api/v1/crashReport"

        const val BASE_PATH = "/api/driver/v1"
        const val FINANCIAL_PATH = "$BASE_PATH /financial"

        /******************************** refresh token Api *********************************/
        const val REFRESH_TOKEN: String = "token"
        const val LOGIN: String = "login"
        const val VERIFICATION: String = "verification"
        const val CHECK: String = "check"

        /******************************** Driver Path *********************************/
        const val GET_APP_INFO: String = "$BASE_PATH/getAppInfo"
        const val ACCEPT_SERVICE: String = "$BASE_PATH/acceptService"
        const val STATION_REGISTER: String = "$BASE_PATH/stationRegister"
        const val EXIT_STATION: String = "$BASE_PATH/exitStationRegister"
        const val FINISH_SERVICE: String = "$BASE_PATH/finishService"
        const val GET_ACTIVE_SERVICE: String = "$BASE_PATH/getActiveService"
        const val GET_FINISH_SERVICE: String = "$BASE_PATH/getFinishService"
        const val GET_NEWS: String = "$BASE_PATH/getNews"
        const val GET_NEWS_DETAILS: String = "$BASE_PATH/getNewsDetail"
        const val GET_WAITING_SERVICE: String = "$BASE_PATH/getWaitingService"

        const val ADD_DRIVER_CHARGE: String = "$FINANCIAL_PATH/addDriverCharge"
        const val GET_FINANCIAL: String = "$FINANCIAL_PATH/getFinancial"


    }

}