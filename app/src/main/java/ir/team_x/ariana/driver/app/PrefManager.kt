package ir.team_x.ariana.driver.app

import android.content.Context
import android.content.SharedPreferences

class PrefManager {

    private var _context: Context? = null
    private val prefName = MyApplication.context.applicationInfo.name
    private var sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    constructor(context: Context) {
        this._context = context
        sharedPreferences = MyApplication.context.getSharedPreferences(prefName, 0)
        editor = sharedPreferences.edit()
    }

    private val USER_NAME = "userName"
    private val SIP_PASSWORD = "sipPassword"
    private val SIP_SERVER = "sipServer"
    private val SIP_NUMBER = "sipNumber"
    private val REFRESH_TOKEN = "refreshToken"
    private val AUTHORIZATION = "authorixation"
    private val ID_TOKEN = "idToken"
    private val LAST_NOTIFICATION = "lastNotification"
    private val KEY_AVA_PID = "AvaPID"
    private val KEY_AVA_TOKEN = "AvaToken"
    private val KEY_USE_ALARM_MANAGER = "userAlarmManager"
    private val KEY_APP_STATUS = "AppStatus"
    private val DRIVER_STATUS = "driverStatus"

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, "")
    }

    fun getDriverStatus(): Boolean {
        return sharedPreferences.getBoolean(DRIVER_STATUS, false)
    }

    fun setDriverStatus(v: Boolean) {
        editor.putBoolean(DRIVER_STATUS, v)
        editor.commit()
    }

    fun useAlarmManager(): Boolean {
        return sharedPreferences.getBoolean(KEY_USE_ALARM_MANAGER, true)
    }

    fun setUseAlarmManager(v: Boolean) {
        editor.putBoolean(KEY_USE_ALARM_MANAGER, v)
        editor.commit()
    }

    fun setAppRun(v: Boolean) {
        editor.putBoolean(KEY_APP_STATUS, v)
        editor.commit()
    }

    fun isAppRun(): Boolean {
        return sharedPreferences.getBoolean(KEY_APP_STATUS, true)
    }

    fun getAvaPID(): Int {
        return sharedPreferences.getInt(KEY_AVA_PID, 10)
    }

    fun setAvaPID(v: Int) {
        editor.putInt(KEY_AVA_PID, v)
        editor.commit()
    }

    fun getAvaToken(): String? {
        return sharedPreferences.getString(KEY_AVA_TOKEN, null)
    }

    fun setAvaToken(v: String?) {
        editor.putString(KEY_AVA_TOKEN, v)
        editor.commit()
    }

    fun setRefreshToken(refreshToken: String) {
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.commit()
    }

    fun getAuthorization(): String? {
        return sharedPreferences.getString(AUTHORIZATION, "")
    }

    fun setAuthorization(authorization: String?) {
        editor.putString(AUTHORIZATION, authorization)
        editor.commit()
    }

    fun getIdToken(): String? {
        return sharedPreferences.getString(ID_TOKEN, "")
    }

    fun setIdToken(idToken: String?) {
        editor.putString(ID_TOKEN, idToken)
        editor.commit()
    }

    fun getLastNotification(): String? {
        return sharedPreferences.getString(LAST_NOTIFICATION, null)
    }

    fun setLastNotification(v: String?) {
        editor.putString(LAST_NOTIFICATION, v)
        editor.commit()
    }

    fun getSipNumber(): String? {
        return sharedPreferences.getString(SIP_NUMBER, "")
    }

    fun setSipNumber(sipNumber: String) {
        editor.putString(SIP_NUMBER, sipNumber)
        editor.commit()
    }

    fun getSipPassword(): String? {
        return sharedPreferences.getString(SIP_PASSWORD, "")
    }

    fun setSipPassword(sipPassword: String) {
        editor.putString(SIP_PASSWORD, sipPassword)
        editor.commit()
    }

    fun getSipServer(): String? {
        return sharedPreferences.getString(SIP_SERVER, "")
    }

    fun setSipServer(sipServer: String) {
        editor.putString(SIP_SERVER, sipServer)
        editor.commit()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(USER_NAME, "")
    }

    fun setUserName(userName: String) {
        editor.putString(USER_NAME, userName)
        editor.commit()
    }


}