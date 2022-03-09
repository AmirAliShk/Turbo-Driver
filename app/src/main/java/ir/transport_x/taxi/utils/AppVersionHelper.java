package ir.transport_x.taxi.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppVersionHelper {

    private static final String TAG = AppVersionHelper.class.getSimpleName();
    Context context;
    PackageInfo pInfo = null;

    public AppVersionHelper(Context context) {
        this.context = context;
        initiate();
    }

    private void initiate() {
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getVersionCode() {
        return pInfo.versionCode;
    }

    public String getVersionName() {
        return pInfo.versionName;
    }


}
