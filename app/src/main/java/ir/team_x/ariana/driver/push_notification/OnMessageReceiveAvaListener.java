package ir.team_x.ariana.driver.push_notification;


import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ir.team_x.ariana.driver.push.AvaReceiver;

/***
 * Created by mohsen mostafaei on 2/16/17.
 */

public class OnMessageReceiveAvaListener extends AvaReceiver {
  public static final String TAG = OnMessageReceiveAvaListener.class.getSimpleName();


  @Override
  public void onPushReceived(Context context, String result) {
    Log.e(TAG, "receive message from global receiver : " + result);
//    if (MyApplication.prefManager.isMainReceiver()) return;
    try {
      JSONObject object = new JSONObject(result);
      new ManagePush().manage(context, object.getString("message"));

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}







