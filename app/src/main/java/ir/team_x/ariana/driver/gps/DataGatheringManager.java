package ir.team_x.ariana.driver.gps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import ir.team_x.ariana.driver.app.Constant;
import ir.team_x.ariana.driver.app.EndPoint;
import ir.team_x.ariana.driver.app.MyApplication;
import ir.team_x.ariana.driver.dialog.GeneralDialog;
import ir.team_x.ariana.driver.okHttp.RequestHelper;
import ir.team_x.ariana.driver.push.AvaCrashReporter;

import static android.content.Context.ALARM_SERVICE;

public class DataGatheringManager extends BroadcastReceiver implements LocationAssistant.Listener {
  public final String TAG = DataGatheringManager.class.getSimpleName();
  Context mContext;
  LocationAssistant assistant;
  boolean isMock = false;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;
    Log.i(TAG, "onReceive: alarmed");
    try {
//      if (MyApplication.prefManager.checkMockLoction()) { // TODO‌ uncomment this
//        Log.i(TAG, "onReceive: mock location is enable");
//        new GeneralDialog()
//                .type(GeneralDialog.ERROR)
//                .message("متاسفانه موقعیت شما صحیح نیست با پشتیبان تماس بگیرید")
//                .secondButton("بستن", null)
//                .cancelable(false)
//                .isSingleMode(true)
//                .show();
//        sendLocationToServer(null, 1);
//        return;
//      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    if (!GPSEnable.isOn()) {
      sendLocationToServer(null, 0);
      return;
    }

    getMyLocation();

  }

  private void getMyLocation() {
    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
      @Override
      public void gotLocation(Location location) {
        if (location == null) {
          Log.i(TAG, "gotLocation: location is null");
          return;
        }
        try {
//          DataHolder.getInstance().setMylat(location.getLatitude());  // TODO‌ uncomment this
//          DataHolder.getInstance().setMylon(location.getLongitude());
          sendLocationToServer(location, 2);
        } catch (Exception e) {
          Log.i(TAG, "gotLocation: Exception : " + e.getMessage());
          AvaCrashReporter.send(e, "DataGatheringManager class ,getMyLocation function");
        }
      }
    };
    MyLocation myLocation = new MyLocation();
    myLocation.getLocation(mContext, locationResult);
  }

  private void sendLocationToServer(Location location, int type) {
    try {

      JSONObject params = new JSONObject();

//      if (type == 0) { //GPS is Off    // TODO‌ uncomment this
//        RequestHelper.builder(EndPoint.HOME_LOCATION)
//                .addParam("lat", 0)
//                .addParam("long", 0)
//                .addParam("speed", 0)
//                .addParam("bearing", 0)
//                .listener(onSendLocation)
//                .post();
//      } else if (type == 1) { //Mock Location Is On
//        RequestHelper.builder(EndPoint.HOME_LOCATION)
//                .addParam("lat", 1)
//                .addParam("long", 1)
//                .addParam("speed", 1)
//                .addParam("bearing", 1)
//                .listener(onSendLocation)
//                .post();
//      } else { //Location Is Ok :)
//        RequestHelper.builder(EndPoint.HOME_LOCATION)
//                .addParam("lat", location.getLatitude())
//                .addParam("long", location.getLongitude())
//                .addParam("speed", location.getSpeed())
//                .addParam("bearing", location.getBearing())
//                .listener(onSendLocation)
//                .post();
//      }

      Log.i(TAG, "sendLocationToServer: PARAMS: " + params);

    } catch (Exception e) {
      e.printStackTrace();
      AvaCrashReporter.send(e, "DataGatheringManager class ,sendLocationToServer function");
    }
  }

  RequestHelper.Callback onSendLocation = new RequestHelper.Callback() {
    @Override
    public void onResponse(Runnable reCall, Object... args) {
//      MyApplication.avaStart();  // TODO‌ uncomment this
//      MyApplication.prefManager.setCheckMockLoction(false);
    }

    @Override
    public void onFailure(Runnable reCall, Exception e) {
      MyApplication.handler.post(new Runnable() {
        @Override
        public void run() {
//          if (new AppStatusHelper().appIsRun(mContext))
//            MyApplication.Toast("عدم توانایی در برقراری ارتباط با سرور", Toast.LENGTH_SHORT);
        }
      });
    }
  };

  public void setAlarmManager(Context context) {
//    if (context == null)   // TODO‌ uncomment this
//      context = this.mContext;
//    Intent intent = new Intent(context, DataGatheringManager.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constant.ALARM_MANAGER_REQ_CODE, intent, 0);
//    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, MyApplication.prefManager.getGpsTimeInterval() * 1000, pendingIntent);
//    assistant = new LocationAssistant(MyApplication.context, this, LocationAssistant.Accuracy.HIGH, MyApplication.prefManager.getGpsTimeInterval() * 1000, false);
//    assistant.start();
//    Log.i(TAG, "setAlarmManager: ");
  }

  public void stopAlarmManager(Context context) {
//    if (context == null)   // TODO‌ uncomment this
//      context = this.mContext;
//    Intent intent = new Intent(context, DataGatheringManager.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constant.ALARM_MANAGER_REQ_CODE, intent, 0);
//    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//    alarmManager.cancel(pendingIntent);
//    if (assistant != null)
//      assistant.stop();
//    Log.i(TAG, "stopAlarmManager: ");
  }

  @Override
  public void onNeedLocationPermission() {

  }

  @Override
  public void onExplainLocationPermission() {

  }

  @Override
  public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

  }

  @Override
  public void onNeedLocationSettingsChange() {

  }

  @Override
  public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

  }

  @Override
  public void onNewLocationAvailable(Location location) {

  }

  @Override
  public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
//    MyApplication.prefManager.setCheckMockLoction(true);   // TODO‌ uncomment this
    Log.i(TAG, "onMockLocationsDetected: Mock Is On");
  }

  @Override
  public void onError(LocationAssistant.ErrorType type, String message) {

  }
}
