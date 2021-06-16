package ir.team_x.ariana.driver.app

import ir.team_x.ariana.driver.BuildConfig

class EndPoint {

    companion object {

        val IP = "http://turbotaxi.ir"
        val HAWKEYE_IP = "http://turbotaxi.ir"
        val PUSH_IP = "http://turbotaxi.ir:6060"

        val IP_PORT = "1810"
        val HAWKEYE_PORT = "1890"

        const val CRASH_REPORT = "http://turbotaxi.ir:6061/api/v1/crashReport"

        val PUSH_ADDRESS = "http://turbotaxi.ir:6060"
        val BASE_PATH = "$IP:$IP_PORT/api/driver/v1"
        private val SYSTEM_PATH = "$BASE_PATH/system"
        val FINANCIAL_PATH = "$BASE_PATH/financial"
        val FINANCIAL_PAY_PATH = "$BASE_PATH/financial/pay"
        val SERVICE_PATH = "$BASE_PATH/service"
        val STATION_PATH = "$BASE_PATH/station"

        /******************************** refresh token Api *********************************/
        const val REFRESH_TOKEN = "token"
        const val LOGIN = "login"
        const val VERIFICATION = "verification"
        const val CHECK = "check"

        /******************************** Driver Path *********************************/
        val GET_APP_INFO = "$BASE_PATH/getAppInfo"
        val ACCEPT_SERVICE = "$BASE_PATH/acceptService"
        val STATION_REGISTER = "$BASE_PATH/stationRegister"
        val EXIT_STATION = "$BASE_PATH/exitStationRegister"
        val FINISH_SERVICE = "$BASE_PATH/finishService"
        val GET_ACTIVE_SERVICE = "$BASE_PATH/getActiveService"
        val GET_FINISH_SERVICE = "$BASE_PATH/getFinishService"
        val GET_NEWS = "$BASE_PATH/getNews"
        val GET_NEWS_DETAILS = "$BASE_PATH/getNewsDetail"
        val GET_WAITING_SERVICE = "$BASE_PATH/getWaitingService"

        val ADD_DRIVER_CHARGE = "$FINANCIAL_PATH/addDriverCharge"
        val GET_FINANCIAL = "$FINANCIAL_PATH/getFinancial"
        val ATM = "$FINANCIAL_PAY_PATH/ATM"

        val ACTIVES = "$SERVICE_PATH/actives"
        val ACCEPT = "$SERVICE_PATH/accept"

        val ENTER_EXIT = "$SYSTEM_PATH/enterExit"

        val REGISTER = "$STATION_PATH/register"
        val EXIT = "$STATION_PATH/exit"

    }

}