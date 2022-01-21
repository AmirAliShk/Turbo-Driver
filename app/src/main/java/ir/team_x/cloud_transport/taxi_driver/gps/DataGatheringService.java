package ir.team_x.cloud_transport.taxi_driver.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ir.team_x.cloud_transport.taxi_driver.R;
import ir.team_x.cloud_transport.taxi_driver.activity.SplashActivity;
import ir.team_x.cloud_transport.taxi_driver.app.AppStatusHelper;
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint;
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;
import ir.team_x.cloud_transport.taxi_driver.app.PrefManager;
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper;

public class DataGatheringService extends Service {

    private static final String TAG = DataGatheringService.class.getSimpleName();
    private static long SEND_DATA_TO_VIEW = 30000;
    LocalBroadcastManager broadcaster;
    protected LocationManager locationManager;
    public static Location network_location;
    public static Location gps_location;
    public Location location;
    Context context;
    private PrefManager prefManager;
    String CHANNEL_ID= "DrDGSForegroundServiceChannel";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        context = this;

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

    }

    String[] permissionsRequired = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotification();
        }

//        MyApplication.prefManager.incrementResetLocationServiceCount();

        prefManager = new PrefManager(this);

//        SEND_DATA_TO_VIEW = prefManager.getGpsTimeInterval() * 1000;// TODO uncomment
        SEND_DATA_TO_VIEW = 15000;
        startSendDataToActivity();

        // if service restart on background we must fill the variable with current context
        if (MyApplication.context == null) {
            Log.w(TAG, "init Context");
            MyApplication.context = this;
        }

        // getAPI socket, if was null create again
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {

            if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MyApplication.currentActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSendDataToActivity();

        //sometime locationListener is Null i don't know when is do
        try {
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onDestroy: ariana driver");
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Bitmap iconNotification = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("DrDGSGroupId", "DrDGSGroupName"));
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "DrDGSChannelName",
                NotificationManager.IMPORTANCE_MIN);
        notificationChannel.enableLights(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        notificationManager.createNotificationChannel(notificationChannel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(new StringBuilder("شما در ").append(getResources().getString(R.string.app_name)).append(" فعال هستید").toString())
//                .setTicker(new StringBuilder(getResources().getString(R.string.app_name)).append("service is running").toString())
//                .setContentText("Touch to open")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
//                .setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
                .setContentIntent(pendingIntent);
        startForeground(1810, builder.build());
    }


    // Send Data to View with BroadCast
    static final public String GDS_RESULT = "ir.team_x.cloud_transport.DataGatheringService";
    static final public String GDS_SPEED = "Service_SPD";
    static final public String GDS_LAT = "Service_lat";
    static final public String GDS_LON = "Service_lon";
    static final public String GDS_BEARING = "Service_bearing";

    private long lastTime = 0;

    public void sendResult() {
        if (Calendar.getInstance().getTimeInMillis() < lastTime + 3000) return;
        lastTime = Calendar.getInstance().getTimeInMillis();

        try {
            if (AppStatusHelper.appIsRun(context)) {
                if (location == null) return;

                Intent intent = new Intent(GDS_RESULT);
                intent.putExtra(GDS_SPEED, location.getSpeed() + "");
                intent.putExtra(GDS_LAT, location.getLatitude() + "");
                intent.putExtra(GDS_LON, location.getLongitude() + "");
                intent.putExtra(GDS_BEARING, location.getBearing() + "");
                broadcaster.sendBroadcast(intent);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MyApplication.prefManager.setLastLocation(latLng);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    // Send Data every 1 seconds to activity
    private Timer timer;

    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            new Thread(() -> {

                if (gps_location != null) {
                    Log.i(TAG, "getAPI location from gps");
                    location = gps_location;
                } else {
                    Log.i(TAG, "getAPI location from netWork");
                    location = network_location;
                }

                if (location == null) {
                    location = getLastLocation();
                    Log.w(TAG, "location is null");
                }
                if (location == null) {
                    return;
                }

                if (prefManager.getApiRequestTime() + 5000 > Calendar.getInstance().getTimeInMillis())
                    return;

                prefManager.setApiRequestTime(Calendar.getInstance().getTimeInMillis());

                // send to server
                emitToServer.run();

                //check pickupGuest
            }).start();
        }
    };

    Runnable emitToServer = new Runnable() {
        @Override
        public void run() {
            Location tempLocation = location;
            JSONObject params = new JSONObject();
            try {
                LocationManager service = (LocationManager) MyApplication.context.getSystemService(Context.LOCATION_SERVICE);
                boolean isTurnOnGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

                RequestHelper requestHelper = RequestHelper.builder(EndPoint.Companion.getSAVE_LOCATION())
                        .setErrorHandling(false)
                        .listener(saveListener);

                if (!isTurnOnGPS) {
                    //location from network
                    requestHelper.addParam("lat", -1)
                            .addParam("lng", -1)
                            .addParam("speed", 0)
                            .addParam("bearing", 0);

                } else {
                    //location from GPS
                    requestHelper.addParam("lat", tempLocation.getLatitude())
                            .addParam("lng", tempLocation.getLongitude())
                            .addParam("speed", tempLocation.getSpeed())
                            .addParam("bearing", tempLocation.getBearing());
                }

                requestHelper.post();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void startSendDataToActivity() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, SEND_DATA_TO_VIEW);
    }

    public void stopSendDataToActivity() {
        if (timer == null) return;
        timer.cancel();
        timer = null;
    }

    RequestHelper.Callback saveListener = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.Companion.avaStart();
            Log.i(TAG, "onResponse saveListener :" + args[0]);
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
//                    if (AppStatusHelper.appIsRun(context))  //TODO uncomment
//                        MyApplication.NetErrorToast();
            });
        }
    };

    private Location getLastLocation() {
        try {
            Location net_location = null, gps_location = null;


            if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MyApplication.currentActivity, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else {
                gps_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                net_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if (gps_location != null && net_location != null) {
                if (gps_location.getTime() > net_location.getTime())
                    return gps_location;
                else
                    return net_location;
            }

            if (gps_location != null) {
                return gps_location;
            }
            if (net_location != null) {
                return net_location;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getLastLocation: no last location : " + e.getMessage());
        }
        return null;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {

            DataGatheringService.gps_location = location;
            sendResult();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            DataGatheringService.network_location = location;
            sendResult();

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}
