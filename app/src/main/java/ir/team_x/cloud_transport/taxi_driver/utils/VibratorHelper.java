package ir.team_x.cloud_transport.taxi_driver.utils;

import android.content.Context;
import android.os.Vibrator;

public class VibratorHelper {
  public static Vibrator vibrator;

  public static void setVibrator(Context context, long[] pattern, int repeat) {
    try {

      vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(pattern, repeat);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void setVibrator(Context context, long[] pattern) {
    try {

      vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(pattern, pattern.length - 1);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void setVibrator(Context context) {
    try {

      vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      long[] pattern = {0, 1000, 800, 1000, 800, 1000, 800, 1000, 800, 1000, 800};
      vibrator.vibrate(pattern, 2);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void stopVibrator() {
    if (vibrator != null)
      vibrator.cancel();
  }
}
