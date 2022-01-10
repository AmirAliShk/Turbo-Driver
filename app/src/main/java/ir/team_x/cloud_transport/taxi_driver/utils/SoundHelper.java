package ir.team_x.cloud_transport.taxi_driver.utils;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.RawRes;

import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;


/**
 * Created by mohsen on 18/10/2016.
 */

public class SoundHelper {

  public static void ringing(Context context, int ringTown,boolean ringing) {
    try {

      if (ringing) {
        return;
      }
//    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      ringtone = RingtoneManager.getRingtone(context, Uri.parse(MyApplication.Companion.getSOUND() + ringTown));
      ringtone.play();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static Ringtone ringtone;
  public static void ringing(Uri uri) {
    try {
//    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      ringtone = RingtoneManager.getRingtone(MyApplication.context, uri);
      ringtone.play();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public static void ringing(@RawRes int ringTown) {
    try {
//    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      ringtone = RingtoneManager.getRingtone(MyApplication.context, Uri.parse(MyApplication.Companion.getSOUND() + ringTown));
      ringtone.play();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public static void stop()
  {
    if(ringtone!=null)
    ringtone.stop();
  }

}
