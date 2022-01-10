package ir.team_x.cloud_transport.driver.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ir.team_x.cloud_transport.driver.app.MyApplication;
import ir.team_x.cloud_transport.driver.push_notification.OnMessageReceiver;

import static android.content.Context.ALARM_SERVICE;

public class AvaReporter {
  public static void Message(Context context, int type, String msg) {
    if (context == null) return;
    try {
      if(MyApplication.prefManager.useAlarmManager()){ // this is always true :|
        Intent intent = new Intent(context, OnMessageReceiver.class);
        intent.putExtra(Keys.KEY_MESSAGE, msg);
        intent.putExtra(Keys.KEY_BROADCAST_TYPE, type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), Keys.ALARM_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
      }else{
        Intent intent = new Intent(context.getPackageName() + "." + Keys.KEY_ACTION_RECEIVE_MESSAGE);
        intent.putExtra(Keys.KEY_MESSAGE, msg);
        intent.putExtra(Keys.KEY_BROADCAST_TYPE, type);
        context.sendBroadcast(intent);
      }

    } catch (Exception e1) {
      AvaCrashReporter.send(e1, 109);
    }
  }



  public static void MessageLog(String msg) {
//    try {
//      Intent intent = new Intent(AvaFactory.getContext().getPackageName() + "." + Keys.KEY_ACTION_RECEIVE_MESSAGE);
//      intent.putExtra(Keys.KEY_MESSAGE, msg);
//      intent.putExtra(Keys.KEY_BROADCAST_TYPE, 1);
//      AvaFactory.getContext().sendBroadcast(intent);
//    } catch (Exception e1) {
//      AvaCrashReporter.send(e1,110);
//    }
  }

}
