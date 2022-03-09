package ir.transport_x.taxi.utils;

/**
 * Created by mohsen on 16/10/2016.
 */
public class PhoneNumberValidation {

  public static boolean isValid(String mobile) {
    if (mobile == null) return false;
    if (mobile.isEmpty()) return false;
    if (mobile.substring(0, 1).equals("0")) {
      mobile = mobile.substring(1);
    }
    String regEx = "[0-9]{10}$";
    return mobile.matches(regEx);
  }

  public static String addZeroFirst(String mobile) {
    if (mobile.substring(0, 1).equals("0")) {
      return mobile;
    } else {
      return "0" + mobile;
    }
  }

}
