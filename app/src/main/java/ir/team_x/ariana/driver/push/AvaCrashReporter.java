package ir.team_x.ariana.driver.push;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.team_x.ariana.driver.app.MyApplication;
import ir.team_x.ariana.driver.okHttp.RequestHelper;
import ir.team_x.ariana.driver.utils.AppVersionHelper;

public class AvaCrashReporter {
  /**
   * arg : first params is CATCH ID
   *
   * @param e
   * @param arg
   */
  public static void send(Exception e, Object... arg) {

//   arg try {
//      if (!MyApplication.prefManager.isCatchCrashReportEnable()) return;
//    } catch (Exception ee) {
//      ee.printStackTrace();
//    }

    try {
      JSONObject customeData = new JSONObject();
      customeData.put("LineCode", MyApplication.prefManager.getDriverId());
      customeData.put("DriverName", MyApplication.prefManager.getUserName());
      customeData.put("IS_CATCH", true);
      customeData.put("CATCH_LINE_NUMBER", AvaSocket.getSocketParams());
      customeData.put("CATCH_ID", arg.length > 0 ? arg[0] : 0);
      customeData.put("CATCH_INPUT_PARAMS", arg.length > 1 ? arg[1] : 0);
      RequestHelper.builder("http://turbotaxi.ir:6061/api/crashReport")
              .addParam("APP_VERSION_CODE", new AppVersionHelper(MyApplication.context).getVersionCode())
              .addParam("APP_VERSION_NAME", new AppVersionHelper(MyApplication.context).getVersionName())
              .addParam("PACKAGE_NAME", MyApplication.context.getPackageName())
              .addParam("PHONE_MODEL", Build.MODEL)
              .addParam("BRAND", Build.BRAND)
              .addParam("ANDROID_VERSION", Build.VERSION.SDK_INT)
              .addParam("TOTAL_MEM_SIZE", "")
              .addParam("AVAILABLE_MEM_SIZE", "")
              .addParam("IS_SILENT", "")
              .addParam("CUSTOM_DATA", customeData)
              .addParam("STACK_TRACE", Log.getStackTraceString(e))
              .addParam("INITIAL_CONFIGURATION", "")
              .addParam("USER_APP_START_DATE", "")
              .addParam("USER_CRASH_DATE", "")
              .addParam("DEVICE_FEATURES", "")
              .post();
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }
}
