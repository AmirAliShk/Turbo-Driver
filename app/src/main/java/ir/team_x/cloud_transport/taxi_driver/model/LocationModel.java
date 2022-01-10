package ir.team_x.cloud_transport.taxi_driver.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

/**
 * Created by Erfanian on 17/09/2017.
 */

public class LocationModel {
  long time;
  String address;
  LatLng latLng;
  String id;
  Date date;
  MarkerOptions markerOption;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public MarkerOptions getMarkerOption() {
    return markerOption;
  }

  public void setMarkerOption(MarkerOptions markerOption) {
    this.markerOption = markerOption;
  }

  public LatLng getLatLng() {
    return latLng;
  }

  public void setLatLng(LatLng latLng) {
    this.latLng = latLng;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

}
