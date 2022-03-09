package ir.transport_x.taxi.push_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import ir.transport_x.taxi.push.Keys;

public class OnMessageReceiver extends BroadcastReceiver {
  public static final String TAG = OnMessageReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    try {
      Bundle bundle = intent.getExtras();
      String result = bundle.getString(Keys.KEY_MESSAGE);
      int type = bundle.getInt(Keys.KEY_BROADCAST_TYPE);

      JSONObject object = new JSONObject(result);
      new ManagePush().manage(context, object.getString("message"));

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}







