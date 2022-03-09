package ir.transport_x.taxi.push_notification;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ir.transport_x.taxi.push.AvaReceiver;

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







