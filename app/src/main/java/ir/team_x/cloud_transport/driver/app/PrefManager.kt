package ir.team_x.cloud_transport.driver.app

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng

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
    private val REFRESH_TOKEN = "refreshToken"
    private val AUTHORIZATION = "authorixation"
    private val ID_TOKEN = "idToken"
    private val LAST_NOTIFICATION = "lastNotification"
    private val KEY_AVA_PID = "AvaPID"
    private val KEY_AVA_TOKEN = "AvaToken"
    private val KEY_USE_ALARM_MANAGER = "userAlarmManager"
    private val KEY_APP_STATUS = "AppStatus"
    private val DRIVER_STATUS = "driverStatus"
    private val STATION_REGISTER_STATUS = "stationRegisterStatus"
    private val KEY_LAST_LAT = "lastLat"
    private val KEY_LAST_LNG = "lastLang"
    private val API_REQUEST_TIME = "requestTime"
    private val CHARGE = "charge"
    private val LOCK_STATUS = "LockStatus"
    private val LOCK_REASONES = "lockReasons"
    private val IBAN = "iban"
    private val NATIONAL_CODE = "nationlCode"
    private val REPEAT_TIME = "repeatTime"
    private val KEY_ACTIVATION_REMAINING_TIME = "activationRemainingTime"
    private val DRIVER_ID = "driverId"
    private val KEY_COUNT_NOTIFICATION = "countNotification"
    private val CARD_NUMBER = "cardNumber"
    private val CARD_NAME = "cardName"

    var cardNumber: String
        get() {
            return sharedPreferences.getString(CARD_NUMBER, "")
        }
        set(cardNumber) {
            editor.putString(CARD_NUMBER, cardNumber)
            editor.commit()
        }

    var cardName: String
        get() {
            return sharedPreferences.getString(CARD_NAME, "")
        }
        set(cardName) {
            editor.putString(CARD_NAME, cardName)
            editor.commit()
        }


    fun setCountNotification(count: Int) {
        editor.putInt(KEY_COUNT_NOTIFICATION, count)
        editor.commit()
    }

    fun getCountNotification(): Int {
        return sharedPreferences.getInt(KEY_COUNT_NOTIFICATION, 0)
    }

    fun setActivationRemainingTime(v: Long) {
        editor.putLong(KEY_ACTIVATION_REMAINING_TIME, v)
        editor.commit()
    }

    fun getActivationRemainingTime(): Long {
        return sharedPreferences.getLong(KEY_ACTIVATION_REMAINING_TIME, 60000)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN, "")
    }

    fun setApiRequestTime(i: Long) {
        editor.putLong(API_REQUEST_TIME, i)
        editor.commit()
    }

    fun getApiRequestTime(): Long {
        return sharedPreferences.getLong(API_REQUEST_TIME, 0)
    }

    fun getLastLocation(): LatLng {
        return LatLng(
            sharedPreferences.getFloat(KEY_LAST_LAT, 36.317265F).toDouble(),
            sharedPreferences.getFloat(KEY_LAST_LNG, 59.562635F).toDouble()
        )
    }

    fun setLastLocation(location: LatLng) {
        editor.putFloat(KEY_LAST_LNG, location.longitude.toFloat())
        editor.putFloat(KEY_LAST_LAT, location.latitude.toFloat())
        editor.commit()
    }

    fun getDriverStatus(): Boolean {
        return sharedPreferences.getBoolean(DRIVER_STATUS, false)
    }

    fun setDriverStatus(v: Boolean) {
        editor.putBoolean(DRIVER_STATUS, v)
        editor.commit()
    }

    fun getRepetitionTime(): Int {
        return sharedPreferences.getInt(REPEAT_TIME, 0)
    }

    fun setRepetitionTime(v: Int) {
        editor.putInt(REPEAT_TIME, v)
        editor.commit()
    }

    fun getDriverId(): Int {
        return sharedPreferences.getInt(DRIVER_ID, 0)
    }

    fun setDriverId(v: Int) {
        editor.putInt(DRIVER_ID, v)
        editor.commit()
    }

    fun getLockStatus(): Int {
        return sharedPreferences.getInt(LOCK_STATUS, 0)
    }

    fun setLockStatus(v: Int) {
        editor.putInt(LOCK_STATUS, v)
        editor.commit()
    }

    fun getLockReasons(): String {
        return sharedPreferences.getString(LOCK_REASONES, "").toString()
    }

    fun setLockReasons(v: String) {
        editor.putString(LOCK_REASONES, v)
        editor.commit()
    }

    fun getIban(): String {
        return sharedPreferences.getString(IBAN, "").toString()
    }

    fun setIban(v: String) {
        editor.putString(IBAN, v)
        editor.commit()
    }

    fun getNational(): String {
        return sharedPreferences.getString(NATIONAL_CODE, "").toString()
    }

    fun setNational(v: String) {
        editor.putString(NATIONAL_CODE, v)
        editor.commit()
    }

    fun getStationRegisterStatus(): Boolean {
        return sharedPreferences.getBoolean(STATION_REGISTER_STATUS, false)
    }

    fun setStationRegisterStatus(v: Boolean) {
        editor.putBoolean(STATION_REGISTER_STATUS, v)
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
        return sharedPreferences.getBoolean(KEY_APP_STATUS, false)
    }

    fun getAvaPID(): Int {
        return sharedPreferences.getInt(KEY_AVA_PID, 3)
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

    fun getCharge(): String? {
        return sharedPreferences.getString(CHARGE, "")
    }

    fun setCharge(charge: String?) {
        editor.putString(CHARGE, charge)
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

    fun getUserName(): String? {
        return sharedPreferences.getString(USER_NAME, "")
    }

    fun setUserName(userName: String) {
        editor.putString(USER_NAME, userName)
        editor.commit()
    }


}