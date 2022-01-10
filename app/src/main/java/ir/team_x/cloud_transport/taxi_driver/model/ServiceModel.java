package ir.team_x.cloud_transport.taxi_driver.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

import ir.team_x.cloud_transport.taxi_driver.R;
import ir.team_x.cloud_transport.taxi_driver.utils.WriteTextOnDrawable;

public class ServiceModel {

  private String serviceID;
  private String callTime;
  private String callDate;
  private Date serviceAcceptTime;
  private String sendTime;
  private String gustName;
  private String gustNumber;
  private String gustNumber1;

  boolean inService;

  private String originAddress;
  private String orginDesc;
  private String destinationDesc;
  private int price;
  private String servicePrice;
  private String description;
  private String status;
  private int count;
  private String ponishmentPrice;
  private String rewardPrice;
  private boolean isAInternetService;
  private String guestServiceCount;
  private String cityLName;
  private String cityPName;
  private int cityCode;

  private LocationModel origin;
  private ArrayList<LocationModel> dests = new ArrayList<>();
  private int customerPrice;
  private long distance;
  private boolean isBack;
  private int stopTime;
  private int statusCode;
  private String customerName;
  private String customerPhoneNumber;
  private String driverName;
  private String driverCode;
  private int customerId = 0;
  private boolean voiceEnable;
  private long arrivalTime;
  private int perDiscount = 0;
  private int maxDiscount = 0;
  private LatLng stPosition;
  private String stName;
  private int stRange;
  private int stCode;

  private String tax;
  private String Commission;
  private String finalPrice;
  private String discount;

  private String cargoType;
  private String carType;
  private String returnBack;
  private String derviceDescription;
  private String fixedDesc;

  public String getFixedDesc() {
    return fixedDesc;
  }

  public void setFixedDesc(String fixedDesc) {
    this.fixedDesc = fixedDesc;
  }

  public String getCargoType() {
    return cargoType;
  }

  public void setCargoType(String cargoType) {
    this.cargoType = cargoType;
  }

  public String getCarType() {
    return carType;
  }

  public void setCarType(String carType) {
    this.carType = carType;
  }

  public String getReturnBack() {
    return returnBack;
  }

  public void setReturnBack(String returnBack) {
    this.returnBack = returnBack;
  }

  public String getDerviceDescription() {
    return derviceDescription;
  }

  public void setDerviceDescription(String derviceDescription) {
    this.derviceDescription = derviceDescription;
  }

  public String getDiscount() {
    return discount;
  }

  public void setDiscount(String discount) {
    this.discount = discount;
  }

  public String getTax() {
    return tax;
  }

  public void setTax(String tax) {
    this.tax = tax;
  }

  public String getCommission() {
    return Commission;
  }

  public void setCommission(String commission) {
    Commission = commission;
  }

  public String getFinalPrice() {
    return finalPrice;
  }

  public void setFinalPrice(String finalPrice) {
    this.finalPrice = finalPrice;
  }

  public boolean isInService() {
    return inService;
  }

  public void setInService(boolean inService) {
    this.inService = inService;
  }

  public String getServicePrice() {
    return servicePrice;
  }

  public void setServicePrice(String servicePrice) {
    this.servicePrice = servicePrice;
  }

  public String getOriginAddress() {
    return originAddress;
  }

  public void setOriginAddress(String originAddress) {
    this.originAddress = originAddress;
  }

  public int getStCode() {
    return stCode;
  }

  public void setStCode(int stCode) {
    this.stCode = stCode;
  }

  public int getStRange() {
    return stRange;
  }

  public void setStRange(int stRange) {
    this.stRange = stRange;
  }

  public String getStName() {
    return stName;
  }

  public void setStName(String stName) {
    this.stName = stName;
  }

  public LatLng getStPosition() {
    return stPosition;
  }

  public void setStPosition(LatLng stPosition) {
    this.stPosition = stPosition;
  }

  public String getDriverCode() {
    return driverCode;
  }

  public void setDriverCode(String driverCode) {
    this.driverCode = driverCode;
  }

  public int getCityCode() {
    return cityCode;
  }

  public void setCityCode(int cityCode) {
    this.cityCode = cityCode;
  }

  public int getPerDiscount() {
    return perDiscount;
  }

  public void setPerDiscount(int perDiscount) {
    this.perDiscount = perDiscount;
  }

  public int getMaxDiscount() {
    return maxDiscount;
  }

  public void setMaxDiscount(int maxDiscount) {
    this.maxDiscount = maxDiscount;
  }

  public long getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(long arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public String getCityLName() {
    return cityLName;
  }

  public void setCityLName(String cityLName) {
    this.cityLName = cityLName;
  }

  public String getCityPName() {
    return cityPName;
  }

  public void setCityPName(String cityPName) {
    this.cityPName = cityPName;
  }

  public boolean isVoiceEnable() {
    return voiceEnable;
  }

  public void setVoiceEnable(boolean voiceEnable) {
    this.voiceEnable = voiceEnable;
  }

  public String getCallDate() {
    return callDate;
  }

  public void setCallDate(String callDate) {
    this.callDate = callDate;
  }

  public String getGustNumber1() {
    return gustNumber1;
  }

  public void setGustNumber1(String gustNumber1) {
    this.gustNumber1 = gustNumber1;
  }

  public Date getServiceAcceptTime() {
    return serviceAcceptTime;
  }

  public void setServiceAcceptTime(Date serviceAcceptTime) {
    this.serviceAcceptTime = serviceAcceptTime;
  }

  public String getRewardPrice() {
    return rewardPrice;
  }

  public void setRewardPrice(String rewardPrice) {
    this.rewardPrice = rewardPrice;
  }

  public String getDriverName() {
    return driverName;
  }

  public void setDriverName(String driverName) {
    this.driverName = driverName;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerPhoneNumber() {
    return customerPhoneNumber;
  }

  public void setCustomerPhoneNumber(String customerPhoneNumber) {
    this.customerPhoneNumber = customerPhoneNumber;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getStopTime() {
    return stopTime;
  }

  public void setStopTime(int stopTime) {
    this.stopTime = stopTime;
  }

  public boolean isBack() {
    return isBack;
  }

  public void setBack(boolean back) {
    isBack = back;
  }

  public long getDistance() {
    return distance;
  }

  public void setDistance(long distance) {
    this.distance = distance;
  }

  public int getCustomerPrice() {
    return customerPrice;
  }

  public void setCustomerPrice(int customerPrice) {
    this.customerPrice = customerPrice;
  }

  public ArrayList<LocationModel> getDests() {
    return dests;
  }

  public void setDests(LocationModel dest) {

    if (this.dests == null)
      this.dests = new ArrayList<>();

    dest.setMarkerOption(new MarkerOptions()
            .position(dest.getLatLng())
            .alpha((float) 0.8)
            .icon(BitmapDescriptorFactory
                    .fromBitmap(WriteTextOnDrawable.write(R.mipmap.pin_pink, (dests.size() + 1) + "",25,3))));
    this.dests.add(dest);
  }

  public LocationModel getOrigin() {
    return origin;
  }

  public void setOrigin(LocationModel origin) {
    origin.setMarkerOption(new MarkerOptions()
            .position(origin.getLatLng())
            .alpha((float) 0.6)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.origin_pin)
            ));
    this.origin = origin;
  }

  public String getPonishmentPrice() {
    return ponishmentPrice;
  }

  public void setPonishmentPrice(String ponishmentPrice) {
    this.ponishmentPrice = ponishmentPrice;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getServiceID() {
    return this.serviceID;
  }

  public void setServiceID(String serviceID) {
    this.serviceID = serviceID;
  }

  public String getCallTime() {
    return this.callTime;
  }

  public void setCallTime(String callTime) {
    this.callTime = callTime;
  }

  public String getGustName() {
    return gustName;
  }

  public void setGustName(String gustName) {
    this.gustName = gustName;
  }

  public String getGustNumber() {
    return gustNumber;
  }

  public void setGustNumber(String gustNumber) {
    this.gustNumber = gustNumber;
  }

  public String getOrginDesc() {
    return orginDesc;
  }

  public void setOrginDesc(String orginDesc) {
    this.orginDesc = orginDesc;
  }

  public String getDestinationDesc() {
    return destinationDesc;
  }

  public void setDestinationDesc(String destinationDesc) {
    this.destinationDesc = destinationDesc;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSendTime() {
    return sendTime;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
  }

  public void setServiceType(boolean isAInternetService) {
    this.isAInternetService = isAInternetService;
  }

  public boolean getServiceType() {
    return this.isAInternetService;
  }

  public String getGuestServiceCount() {
    return guestServiceCount;
  }

  public void setGuestServiceCount(String guestServiceCount) {
    this.guestServiceCount = guestServiceCount;
  }
}
