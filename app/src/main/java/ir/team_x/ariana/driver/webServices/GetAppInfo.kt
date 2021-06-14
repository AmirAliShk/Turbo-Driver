package ir.team_x.ariana.driver.webServices

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.fragment.LoginFragment
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.AppVersionHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.ScreenHelper
import org.json.JSONObject

public class GetAppInfo {

    @SuppressLint("HardwareIds")
    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.getRefreshToken().equals("")) {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, LoginFragment())
                    .setAddToBackStack(false)
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
                    val status = splashJson.getBoolean("status")
                    val message = splashJson.getString("message")

                    val dataObject = splashJson.getJSONObject("data")
                    val isBlock = dataObject.getInt("isBlock")
                    val updateAvialable = dataObject.getInt("updateAvialable")
                    val forceUpdate = dataObject.getInt("forceUpdate")
                    val updateUrl = dataObject.getString("updateUrl")
                    MyApplication.prefManager.setUserName(
                        dataObject.getString("firstName") + " " + dataObject.getString(
                            "lastName"
                        )
                    )
                    MyApplication.prefManager.setSipNumber(dataObject.getString("sipNumber"))
                    MyApplication.prefManager.setSipPassword(dataObject.getString("sipPassword"))
//                    MyApplication.prefManager.setSipServer(dataObject.getString("sipServer"))


                } catch (e: Exception) {
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
    }
}