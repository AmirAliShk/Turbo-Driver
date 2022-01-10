package ir.team_x.cloud_transport.taxi_driver.push_notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.team_x.cloud_transport.taxi_driver.R;
import ir.team_x.cloud_transport.taxi_driver.activity.CancelServiceActivity;
import ir.team_x.cloud_transport.taxi_driver.activity.GetServiceActivity;
import ir.team_x.cloud_transport.taxi_driver.activity.SplashActivity;
import ir.team_x.cloud_transport.taxi_driver.app.AppStatusHelper;
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;
import ir.team_x.cloud_transport.taxi_driver.app.PrefManager;
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog;
import ir.team_x.cloud_transport.taxi_driver.model.RegisterModel;
import ir.team_x.cloud_transport.taxi_driver.model.ServiceModel;
import ir.team_x.cloud_transport.taxi_driver.push.PushDataHolder;
import ir.team_x.cloud_transport.taxi_driver.utils.SoundHelper;
import ir.team_x.cloud_transport.taxi_driver.utils.VibratorHelper;
import ir.team_x.cloud_transport.taxi_driver.dialog.GetServiceDialog;

public class ManagePush {

    private static final String TAG = ManagePush.class.getSimpleName();

    //    {"messageId":6725706,"message":"2^13:00:00^29286045^0^محدوده^احمداباد بابک 7 پ 55^مجد4^10000","projectId":3,"userId":"7650"}
    public void manage(final Context context, String pushMessage) {
        Log.i(TAG, "onPushReceived: message received node : " + pushMessage);
        String[] dataArray = pushMessage.split("\\^");
        final String type = dataArray[0];
        final PrefManager prefManager = new PrefManager(context);
        Log.i(TAG, "onPushReceived: type : " + type);

        MyApplication.context = context;
//    MyApplication.context = context;

        String message;
        switch (type) {
            case "1":
                // message
                try {
                    SoundHelper.ringing(context, R.raw.notification, false);
                    message = dataArray[1];
                    if (MyApplication.prefManager.isAppRun())
                        new GeneralDialog().message(message).firstButton("باشه",null).show();
                    else {
                        PushDataHolder.getInstance().setMessage(message);
                        sendNotification(message, context, true, 4, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "2":
                // getServiceTurbo info
                SoundHelper.ringing(context, R.raw.service, false);
                if (MyApplication.prefManager.isAppRun()) {
                    Log.i(TAG, "manage: app is running");
                    GetServiceDialog dialog = new GetServiceDialog();
                    dialog.show(getSerivceInfo(dataArray));
                } else {
                    Log.i(TAG, "manage: app is not running");

                    if (MyApplication.currentActivity != null) {
                        MyApplication.currentActivity.finish();
                    }
                    VibratorHelper.setVibrator(context, new long[]{100, 100, 100}, -1);
                    Intent in = new Intent(context, GetServiceActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    in.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    ServiceModel mdoel = getSerivceInfo(dataArray);
                    in.putExtra("CallTime", mdoel.getCallTime());
                    in.putExtra("ServiceType", mdoel.getServiceType());
                    in.putExtra("ServiceID", mdoel.getServiceID());
                    in.putExtra("OrginDesc", mdoel.getOrginDesc());
                    in.putExtra("originAddress", mdoel.getOriginAddress());
                    in.putExtra("destinationAddress", mdoel.getDestinationDesc());
                    in.putExtra("price", mdoel.getServicePrice());
                    in.putExtra("inService", mdoel.isInService());
                    in.putExtra("carType", mdoel.getCarType());
                    in.putExtra("cargoType", mdoel.getCargoType());
                    in.putExtra("description", mdoel.getDescription());
                    in.putExtra("fixesDescription", mdoel.getFixedDesc());
                    in.putExtra("returnBack", mdoel.getReturnBack());
                    context.startActivity(in);
                }
                break;

            case "3":
//              {"messageId":1111,"message":"3^ سرویس شما به � کنسل شد","projectId":"3","userId":234}
                // cancel
                String cancelMessage = dataArray[1];

                if (AppStatusHelper.appIsRun(context)) {
                    Intent in = new Intent(MyApplication.currentActivity, CancelServiceActivity.class);
                    in.putExtra("cancelMessage", cancelMessage);

                    MyApplication.currentActivity.finish();
                    MyApplication.currentActivity.startActivity(in);
                } else {
                    Intent in = new Intent(MyApplication.context, CancelServiceActivity.class);
                    in.putExtra("cancelMessage", cancelMessage);

                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MyApplication.context.startActivity(in);
                }

                break;
            case "4":
                // freeService
//                if (!MyApplication.prefManager.isActiveTurboService()) {
//
                String freeService = dataArray[1];
//                new GeneralDialog().message(freeService).firstButton("باشه", null).show();

                    if (MyApplication.prefManager.isAppRun()) {
//
//                        if (!prefManager.isMuteFreeServiceAlarm()) {
//                            try {
//                                if (MainFragment.cRed == null) return;
//                                MainFragment.cRed.setVisibility(View.VISIBLE);
//                                Animation anim = AnimationUtils.loadAnimation(MyApplication.context, R.anim.fade_in_out);
//                                anim.setRepeatCount(10);
//                                MainFragment.cRed.startAnimation(anim);
////
//                                anim.setAnimationListener(new Animation.AnimationListener() {
//                                    @Override
//                                    public void onAnimationStart(Animation animation) {
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animation animation) {
//                                        MainFragment.cRed.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animation animation) {
//
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
                            SoundHelper.ringing(context, R.raw.service, false);
//                            new ToastFragment().addToast(freeService, MyApplication.currentActivity);
                            MyApplication.Companion.showSnackBar(freeService);
//                        }
                    } else {
//                        SoundHelper.ringing(context, R.raw.service, !prefManager.isMuteFreeServiceAlarm());
//                        PushDataHolder.getInstance().setFreeServiceMessage(freeService);
                        sendNotification("به بخش بار های ازاد مراجه کنید.", context, false, 2, true);                    }
//                }
                break;

        }
    }

    private void sendNotification(String message, Context context, boolean vibrate, int notificationId, boolean isHighPriority) {

        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Intent intent1 = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        if (vibrate) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.logo_type)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logoariana))
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{2000, 1000})
                    .setSound(Uri.parse(MyApplication.Companion.getSOUND()  + R.raw.notification))
                    .setContentIntent(pendingIntent);


            if (isHighPriority) notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setSmallIcon(R.mipmap.logo_type);
            notificationBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
            notificationBuilder.setContentText(message);

            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logoariana));
            if (isHighPriority) notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(Uri.parse(MyApplication.Companion.getSOUND()  + R.raw.notification));
            notificationBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
            //ThreadLocalRandom.current().nextInt(1, 100 + 1)
        }

    }

    private ServiceModel getSerivceInfo(String[] dataArray) {
        String time = dataArray[1];
        String serviceId = dataArray[2];
        String serviceType = dataArray[3];
        String originAddress = dataArray[4];
        String destinationAddress = dataArray[5];
        String price = dataArray[6];
        String carType = dataArray[7];
        String cargoStr = dataArray[8];
        String returnBack = dataArray[9];
        String describeService = dataArray[10];
        String fixDescribeService = dataArray[11];

        Log.i(TAG, "getSerivceInfo:\n 1:time: " + time + "\n‌" + "2:serviceId: " + serviceId + "\n" + "3:serviceType: " + serviceType + "\n"
                + "4:originAddress: " + originAddress + "\n" +
                "5:destinationAddress: " + destinationAddress + "\n" + "6:price: " + price
                + "\n" + "7:carType: " + carType
                + "\n" + "8:cargoStr: " + cargoStr
                + "\n" + "9:returnBack: " + returnBack
                + "\n" + "10:describeService: " + describeService
                + "\n" + "11:fixDescribeService: " + fixDescribeService
        );

        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setCallTime(time);
        serviceModel.setServiceID(serviceId);
        serviceModel.setInService(serviceType.trim().equals("1"));
        serviceModel.setOriginAddress(originAddress);
        serviceModel.setDestinationDesc(destinationAddress);
        serviceModel.setServicePrice(price);
        serviceModel.setCarType(carType);
        serviceModel.setCargoType(cargoStr);
        serviceModel.setReturnBack(returnBack);
        serviceModel.setDescription(describeService);
        serviceModel.setFixedDesc(fixDescribeService);
        return serviceModel;
    }

    private RegisterModel getRegisterModel(String[] dataArray) {

        try {
            String title = dataArray[1];
            String message = dataArray[2];
            String station = dataArray[3];

            // Here send to view or notification
            RegisterModel registerModel = new RegisterModel();
            registerModel.setTitle(title);
            registerModel.setMessage(message);
            registerModel.setStation(station);
            return registerModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getServiceTurbo(JSONObject object) {


        try {
            int acceptTime = object.getInt("acceptTime");
            String serviceId = object.getInt("serviceId") + "";
            String originAddress = object.getString("originAddress");
            String lastDestAddress = object.getString("lastDestAddress");
            int price = object.getInt("price");
            int customerId = object.getInt("customerId");
            double originLat = 0;
            double originLng = 0;
            double firstDestLat = 0;
            double firstDestLng = 0;
            double secondDestLat = 0;
            double secondDestLng = 0;
            try {
                originLat = object.getDouble("originAddressLat");
                originLng = object.getDouble("originAddressLng");
                JSONArray array = object.getJSONArray("dests");

                if (array.length() > 0) {
                    firstDestLat = array.getJSONObject(0).getDouble("destLat");
                    firstDestLng = array.getJSONObject(0).getDouble("destLng");
                }
                if (array.length() > 1) {
                    secondDestLat = array.getJSONObject(1).getDouble("destLat");
                    secondDestLng = array.getJSONObject(1).getDouble("destLng");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            String desc = object.getString("desc");

            showServiceNoti(acceptTime, serviceId, MyApplication.context, false);

//            if (AppStatusHelper.appIsRun(MyApplication.context)) {
//                Log.i(TAG, "getServiceTurbo: activity is run");
//                Intent in = new Intent(MyApplication.currentActivity, GetServiceActivityTurbo.class);
//                in.putExtra("serviceId", serviceId);
//                in.putExtra("acceptTime", acceptTime);
//                in.putExtra("originAddress", originAddress);
//                in.putExtra("lastDestAddress", lastDestAddress);
//                in.putExtra("price", price);
//                in.putExtra("desc", desc);
//                in.putExtra("customerId", customerId);
//                in.putExtra("originLat", originLat);
//                in.putExtra("originLng", originLng);
//                in.putExtra("firstDestLat", firstDestLat);
//                in.putExtra("firstDestLng", firstDestLng);
//                in.putExtra("secondDestLat", secondDestLat);
//                in.putExtra("secondDestLng", secondDestLng);
//                MyApplication.currentActivity.finish();
//                MyApplication.currentActivity.startActivity(in);
//
//            } else {
//                Log.i(TAG, "getServiceTurbo: activity is not run");
//
//                Intent in = new Intent(MyApplication.context, GetServiceActivityTurbo.class);
//                in.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                in.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                in.putExtra("serviceId", serviceId);
//                in.putExtra("acceptTime", acceptTime);
//                in.putExtra("originAddress", originAddress);
//                in.putExtra("lastDestAddress", lastDestAddress);
//                in.putExtra("price", price);
//                in.putExtra("desc", desc);
//                in.putExtra("customerId", customerId);
//                in.putExtra("originLat", originLat);
//                in.putExtra("originLng", originLng);
//                in.putExtra("firstDestLat", firstDestLat);
//                in.putExtra("firstDestLng", firstDestLng);
//                in.putExtra("secondDestLat", secondDestLat);
//                in.putExtra("secondDestLng", secondDestLng);
//                MyApplication.context.startActivity(in);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getServiceInfo(int serviceId) {
//        LoadingDialog.makeLoader();
//        RequestHelper.loadBalancingBuilder(EndPoints.TRIP_INFO)
//                .addPath(serviceId + "")
//                .listener(new RequestHelper.Callback() {
//                    @Override
//                    public void onResponse(Runnable reCall, Object... args) {
//                        try {
//                            LocalBroadcastManager broadcaster;
//                            broadcaster = LocalBroadcastManager.getInstance(MyApplication.context);
//                            Intent intent = new Intent(KeyContainer.GET_SERVICE_INFO);
//                            intent.putExtra(KeyContainer.RESULT, args[0].toString());
//                            broadcaster.sendBroadcast(intent);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Runnable reCall, Exception e) {
//
//                    }
//                })
//                .get();
    }

    public static void showServiceNoti(int acceptTime, String serviceId, Context context, boolean cancelable) {

        Intent in;
        in = new Intent(context, GetServiceActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        in.putExtra("serviceId", serviceId);
        in.putExtra("acceptTime", acceptTime);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, in, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ariana)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logoariana))
                .setContentIntent(pendingIntent)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("سرویس جدید در انتظار شماست")
                .setOngoing(!cancelable);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101 /* ID of notification */, notificationBuilder.build());

        MyApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoundHelper.stop();
                VibratorHelper.stopVibrator();
                dismissServiceNoti();
            }
        }, acceptTime * 1000 + 30000);
    }

    public static void dismissServiceNoti() {
        try {
            NotificationManager notificationManager = (NotificationManager) MyApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(101);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGeneralNoti(String title, String message, Context context, boolean cancelable) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent;
        intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Log.i(TAG, "showNotiForServer: start show noti on notibar");
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ariana)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logoariana))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle(title)
                .setOngoing(!cancelable)
                .setVibrate(new long[]{1000, 1000, 200})
                .setSound(Uri.parse(MyApplication.Companion.getSOUND() + R.raw.edit_sound));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
