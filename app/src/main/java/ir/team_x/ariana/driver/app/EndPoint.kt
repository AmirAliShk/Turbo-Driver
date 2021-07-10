package ir.team_x.ariana.driver.app


class EndPoint {

    companion object {

        val IP = "http://turbotaxi.ir"
        val HAWKEYE_IP = "http://turbotaxi.ir"
        val PUSH_IP = "http://turbotaxi.ir:6060"

        val IP_PORT = "1810"
        val HAWKEYE_PORT = "1890"

        const val CRASH_REPORT = "http://turbotaxi.ir:6061/api/v1/crashReport"

        const val PUSH_ADDRESS = "http://turbotaxi.ir:6060"
        val BASE_PATH = "$IP:$IP_PORT/api/driver/v1"
        private val FINANCIAL_PATH = "$BASE_PATH/financial"
        private val FINANCIAL_PAY_PATH = "$BASE_PATH/financial/pay"
        private val FINANCIAL_PAYS_PATH = "$BASE_PATH/financial/pays"
        private val SERVICE_PATH = "$BASE_PATH/service"
        private val STATION_PATH = "$BASE_PATH/station"
        val LOCATION_PATH = "$IP:$IP_PORT/api/driver/v1/location/car"

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
        val GET_NEWS = "$BASE_PATH/news"
        val STATUS = "$BASE_PATH/status"
        val ENTER_EXIT = "$BASE_PATH/enterExit"

        val GET_WAITING_SERVICE = "$BASE_PATH/getWaitingService"
        val ADD_DRIVER_CHARGE = "$FINANCIAL_PATH/addDriverCharge"
        val GET_FINANCIAL = "$FINANCIAL_PATH/getFinancial"
        val CHARGE = "$FINANCIAL_PATH/charge"

        val ACCOUNT_REP = "$FINANCIAL_PATH"
        val ATM = "$FINANCIAL_PAY_PATH/ATM"
        val GET_ATM = "$FINANCIAL_PAYS_PATH/ATM"
        val ACTIVES = "$SERVICE_PATH/actives"
        val ACCEPT = "$SERVICE_PATH/accept"
        val FINISH = "$SERVICE_PATH/finish"
        val FINISHED = "$SERVICE_PATH/finished"
        val CANCEL = "$SERVICE_PATH/cancel"
        val WAITING = "$SERVICE_PATH/waiting"

        val REGISTER = "$STATION_PATH/register"
        val EXIT = "$STATION_PATH/exit"

        val SAVE_LOCATION = "$LOCATION_PATH/save"

    }

}