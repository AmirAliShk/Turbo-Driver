package ir.team_x.cloud_transport.taxi_driver.webServices

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.activity.MainActivity
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.login.VerificationFragment
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.utils.AppVersionHelper
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.ScreenHelper
import org.json.JSONObject

class GetAppInfo {

    @SuppressLint("HardwareIds")
    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.getRefreshToken().equals("")) {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, VerificationFragment())
                    .setAddToBackStack(false)
                    .setFrame(android.R.id.content)
                    .add()
            } else {
                val android_id = Settings.Secure.getString(
                    MyApplication.currentActivity.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                var deviceInfo: JSONObject? = null
                deviceInfo?.put("MODEL", Build.MODEL);
                deviceInfo?.put("HARDWARE", Build.HARDWARE);
                deviceInfo?.put("BRAND", Build.BRAND);
                deviceInfo?.put("DISPLAY", Build.DISPLAY);
                deviceInfo?.put("BOARD", Build.BOARD);
                deviceInfo?.put("SDK_INT", Build.VERSION.SDK_INT);
                deviceInfo?.put("BOOTLOADER", Build.BOOTLOADER);
                deviceInfo?.put("DEVICE", Build.DEVICE);
                deviceInfo?.put(
                    "DISPLAY_HEIGHT",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).height
                )
                deviceInfo?.put(
                    "DISPLAY_WIDTH",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).width
                )
                deviceInfo?.put(
                    "DISPLAY_SIZE",
                    ScreenHelper.getScreenSize(MyApplication.currentActivity)
                )
                deviceInfo?.put("ANDROID_ID", android_id)

                RequestHelper.builder(EndPoint.GET_APP_INFO)
                    .addParam("versionCode", AppVersionHelper(MyApplication.context).versionCode)
                    .addParam("deviceInfo", deviceInfo)
                    .addParam("applicationId", MyApplication.context.packageName)
                    .listener(getAppInfoCallBack)
                    .post()
            }
        } catch (e: Exception) {

        }
    }

    private val getAppInfoCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val splashJson = JSONObject(args[0].toString())
                    val success = splashJson.getBoolean("success")
                    val message = splashJson.getString("message")
                    if (success) {
                        val dataObject = splashJson.getJSONObject("data")
                        val isActive = dataObject.getInt("isActive")
                        val isLock = dataObject.getInt("isLock")
                        val reasonDescription = dataObject.getString("reasonDescription")
                        val updateAvialable = dataObject.getInt("updateAvialable")
                        val forceUpdate = dataObject.getInt("forceUpdate")
                        val updateUrl = dataObject.getString("updateUrl")
                        val driverId = dataObject.getInt("driverId")
                        MyApplication.prefManager.setUserName(
                            dataObject.getString("firstName") + " " + dataObject.getString(
                                "lastName"
                            )
                        )
                        MyApplication.prefManager.setLockStatus(isLock)
                        MyApplication.prefManager.setLockReasons(reasonDescription)
                        MyApplication.prefManager.setIban(dataObject.getString("IBAN"))
                        MyApplication.prefManager.setCharge(dataObject.getString("charge"))
                        MyApplication.prefManager.setNational(dataObject.getString("nationalCode"))
                        MyApplication.prefManager.setAvaPID(dataObject.getInt("pushId"))
                        MyApplication.prefManager.setAvaToken(dataObject.getString("pushToken"))
                        MyApplication.prefManager.setDriverId(driverId)
                        MyApplication.prefManager.setDriverStatus(dataObject.getInt("isEnter") == 1)
                        MyApplication.prefManager.setCountNotification(dataObject.getInt("countNews"))
                        MyApplication.prefManager.cardNumber = dataObject.getString("accountCard")
                        MyApplication.prefManager.cardName = dataObject.getString("accountName")
                        MyApplication.prefManager.pricing = dataObject.getInt("pricing")
                        MyApplication.prefManager.supportNumber = dataObject.getString("phoneNumberSupport")
                        MyApplication.prefManager.onlineUrl = dataObject.getString("onlineUrl")
                        MyApplication.prefManager.aboutUs = dataObject.getString("aboutUs")
                        MyApplication.prefManager.pushUrl = dataObject.getString("pushUrl")

                        if (isActive == 0) {
                            GeneralDialog()
                                .message("اکانت شما توسط سیستم مسدود شده است")
                                .secondButton("خروج از برنامه") {
                                    MyApplication.currentActivity.finish()
                                }
                                .show()
                            return@post
                        }

                        if (updateAvialable == 1) {
                            update(forceUpdate == 1, updateUrl)
                            return@post
                        }

                        MyApplication.avaStart()
                        MyApplication.currentActivity.finish()
                        MyApplication.currentActivity.startActivity(
                            Intent(
                                MyApplication.currentActivity,
                                MainActivity::class.java
                            )
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
    }

    fun update(isForce: Boolean, url: String) {
        if (isForce) {
            GeneralDialog()
                .message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید")
                .firstButton("به روز رسانی") {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    MyApplication.currentActivity.startActivity(i)
                    MyApplication.currentActivity.finish()
                }.secondButton("بستن") {
                    MyApplication.currentActivity.finish()
                }.show()
        } else {
            GeneralDialog()
                .message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید")
                .firstButton("به روز رسانی") {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    MyApplication.currentActivity.startActivity(i)
                    MyApplication.currentActivity.finish()
                }.secondButton("فعلا نه") {
                    MyApplication.currentActivity.startActivity(
                        Intent(
                            MyApplication.currentActivity,
                            MainActivity::class.java
                        )
                    )
                    MyApplication.currentActivity.finish()
                }.show()
        }
    }

}