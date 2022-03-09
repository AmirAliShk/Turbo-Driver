package ir.transport_x.taxi.gps;

import android.content.Context;
import android.location.LocationManager;
import ir.transport_x.taxi.app.MyApplication;

public class GPSEnable {

  public static boolean isOn() {
    LocationManager service = (LocationManager) MyApplication.context.getSystemService(Context.LOCATION_SERVICE);
    return service.isProviderEnabled(LocationManager.GPS_PROVIDER);

  }

}
