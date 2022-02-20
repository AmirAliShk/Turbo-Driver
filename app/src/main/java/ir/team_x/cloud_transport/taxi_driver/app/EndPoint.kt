package ir.team_x.cloud_transport.taxi_driver.app


class EndPoint {

    companion object {
        val IP = "http://transport.team-x.ir:7070"
        val HAWKEYE_IP = "http://transport.team-x.ir:7071"
        const val CRASH_REPORT = "http://transport.team-x.ir:6061/api/v1/crashReport"
        val RULL = "http://transport.team-x.ir:7073/rule/taxi"
        const val PUSH_ADDRESS = "http://transport.team-x.ir:6060"
        val BASE_PATH = "$IP/api/taxi/v1"
        private val FINANCIAL_PATH = "$BASE_PATH/financial"
        private val FINANCIAL_PAY_PATH = "$BASE_PATH/financial/pay"
        private val FINANCIAL_PAYS_PATH = "$BASE_PATH/financial/pays"
        private val SERVICE_PATH = "$BASE_PATH/service"
        private val STATION_PATH = "$BASE_PATH/station"
        private val LOCATION_PATH = "$BASE_PATH/location/car"
//        private val LOCATION_PATH = "$IP/api/driver/v1/location/car"

        private val HAWKEYE_TOKEN_PATH = "${HAWKEYE_IP}/api/user/v1/"
        private val HAWKEYE_LOGIN_PATH = "${HAWKEYE_IP}/api/user/v1/login/phone/"

        /******************************** refresh token Api *********************************/
        val REFRESH_TOKEN = "${HAWKEYE_TOKEN_PATH}token"
        val CHECK = "${HAWKEYE_LOGIN_PATH}check"
        val VERIFICATION = "${HAWKEYE_LOGIN_PATH}verification"

        /******************************** Driver Path *********************************/
        val GET_APP_INFO = "$BASE_PATH/appInfo"
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
        val BILL = "$FINANCIAL_PATH/bill"
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
        val STATION = "$STATION_PATH/countService"

        val SAVE_LOCATION = "$LOCATION_PATH/save"
        val PAYMENT = "http://transport.team-x.ir/credit/drivercharge/"

    }

}