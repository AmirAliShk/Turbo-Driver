package ir.transport_x.taxi.push_notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import ir.transport_x.taxi.R;
import ir.transport_x.taxi.activity.CancelServiceActivity;
import ir.transport_x.taxi.activity.GetServiceActivity;
import ir.transport_x.taxi.activity.SplashActivity;
import ir.transport_x.taxi.app.AppStatusHelper;
import ir.transport_x.taxi.app.MyApplication;
import ir.transport_x.taxi.app.PrefManager;
import ir.transport_x.taxi.dialog.AvailableServiceDialog;
import ir.transport_x.taxi.dialog.GeneralDialog;
import ir.transport_x.taxi.model.ServiceModel;
import ir.transport_x.taxi.push.PushDataHolder;
import ir.transport_x.taxi.utils.SoundHelper;
import ir.transport_x.taxi.utils.VibratorHelper;

public class ManagePush {

    private static final String TAG = ManagePush.class.getSimpleName();

    //    {"messageId":6725706,"message":"2^13:00:00^29286045^0^محدوده^احمداباد بابک 7 پ 55^مجد4^10000","projectId":3,"userId":"7650"}
    public void manage(final Context context, String pushMessage) {
        Log.i(TAG, "onPushReceived: message received node : " + pushMessage);
        String[] dataArray = pushMessage.split("\\^");
        final String type = dataArray[0];
        final PrefManager prefManager = new PrefManager();
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
                if(MyApplication.prefManager.getMuteNotifications()){
                    Log.i(TAG, "onCreateView:OOOOOOOOOOOOO returned");
                    return;//when it is (true) it means notifications are muted
                }
                    SoundHelper.ringing(context, R.raw.service, false);
                    if (MyApplication.prefManager.isAppRun()) {
                        Log.i(TAG, "manage: app is running");
                        AvailableServiceDialog.Companion.show(getSerivceInfo(dataArray));
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
                        sendNotification("به بخش سرویس های ازاد مراجه کنید.", context, false, 2, true);                    }
//                }
                break;

            case "5":
                //todo for edit internet Service
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
                    .setSmallIcon(R.drawable.ic_x_blue)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_x_blue))
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
            notificationBuilder.setSmallIcon(R.drawable.ic_x_blue);
            notificationBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
            notificationBuilder.setContentText(message);

            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_x_blue));
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
        serviceModel.setInService(serviceType.trim().equals("2"));
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

}
