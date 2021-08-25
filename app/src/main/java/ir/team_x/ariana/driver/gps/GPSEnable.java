package ir.team_x.ariana.driver.gps;

import android.content.Context;
import android.location.LocationManager;

import ir.team_x.ariana.driver.app.MyApplication;

public class GPSEnable {

  public static boolean isOn() {
    LocationManager service = (LocationManager) MyApplication.context.getSystemService(Context.LOCATION_SERVICE);
    return service.isProviderEnabled(LocationManager.GPS_PROVIDER);

  }

}
