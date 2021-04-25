package ir.team_x.ariana.driver.app

import ir.team_x.ariana.driver.BuildConfig


public class EndPoint {

    companion object {

        public val IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"
        val HAWKEYE_IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"
        val PUSH_IP = if (BuildConfig.DEBUG) "http://turbotaxi.ir" else "http://turbotaxi.ir"

        val IP_PORT = if (BuildConfig.DEBUG) "1881" else "1881"
        val HAWKEYE_PORT = if (BuildConfig.DEBUG) "1890" else "1890"

        val CRASH_REPORT = "http://turbotaxi.ir:6061/api/v1/crashReport"

        /******************************** refresh token Api *********************************/
        const val REFRESH_TOKEN: String = "token"
        const val LOGIN: String = "login"
        const val VERIFICATION: String = "verification"
        const val CHECK: String = "check"
    }

}