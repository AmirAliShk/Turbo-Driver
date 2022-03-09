package ir.transport_x.taxi.utils;

import android.content.Intent;
import android.net.Uri;

import ir.transport_x.taxi.app.MyApplication;

/** * Created by mohsen on 16/10/2016.*/
public class CallHelper {

  public static void make(String number) {

    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:"+number));
    MyApplication.currentActivity.startActivity(intent);
  }
}
