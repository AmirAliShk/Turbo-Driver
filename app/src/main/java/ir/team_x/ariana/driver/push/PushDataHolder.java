package ir.team_x.ariana.driver.push;

import ir.team_x.ariana.driver.model.RegisterModel;
import ir.team_x.ariana.driver.model.ServiceModel;

/**
 * Created by mohsen mostafaei on 29/06/2016.
 */

public class PushDataHolder {

  private static final PushDataHolder INSTANCE = new PushDataHolder();
  private boolean haveSomethingToShow = false;
  private boolean isMessage = false;
  private boolean isFreeServiceMessage = false;
  private boolean isRegister = false;
  private boolean isService = false;
  private boolean isCancelMessage = false;
  private String message;
  private ServiceModel serviceModel;
  private RegisterModel registerModel;

  public static PushDataHolder getInstance() {
    return INSTANCE;
  }

  public boolean isMessageAvailable() {
    return isMessage;
  }

  public boolean isServiceAvailable() {
    return isService;
  }

  public boolean isFreeServiceAvailable() {
    return isFreeServiceMessage;
  }

  public boolean isCancelAvailable() {
    return isCancelMessage;
  }

  public boolean isRegisterAvailable() {
    return isRegister;
  }

  public void setMessage(String message) {
    this.message = message;
    haveSomethingToShow = true;
    isMessage = true;
  }

  public void setServiceModel(ServiceModel serviceModel) {
    this.serviceModel = serviceModel;
    haveSomethingToShow = true;
    isService = true;
  }

  public void setRegisterModel(RegisterModel registerModel) {
    this.registerModel = registerModel;
    haveSomethingToShow = true;
    isRegister = true;
  }

  public void setCancelMessage(String message) {
    this.message = message;
    haveSomethingToShow = true;
    isCancelMessage = true;
  }

  public void setFreeServiceMessage(String message) {
    this.message = message;
    haveSomethingToShow = true;
    isFreeServiceMessage = true;
  }

  public ServiceModel getServiceModel() {
    clearHistory();
    return this.serviceModel;
  }

  public RegisterModel getRegisterModel() {
    clearHistory();
    return this.registerModel;
  }

  public String getMessage() {
    clearHistory();
    return this.message;
  }

  public String getCancelMessage() {
    clearHistory();
    return this.message;
  }

  public String getFreeServiceMessage() {
    clearHistory();
    return this.message;
  }

  private void clearHistory() {
    haveSomethingToShow = false;
    isMessage = false;
    isFreeServiceMessage = false;
    isRegister = false;
    isService = false;
    isCancelMessage = false;
  }

}