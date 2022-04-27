package ir.transport_x.taxi.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import ir.transport_x.taxi.R;
import ir.transport_x.taxi.app.MyApplication;
import ir.transport_x.taxi.push.AvaCrashReporter;
import ir.transport_x.taxi.utils.ResourceHelper;

public class LoadingDialog {

    private static final String TAG = LoadingDialog.class.getSimpleName();
    private static Dialog ldialog;
    static boolean cancelable = false;

    public LoadingDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public static void makeLoader() {
        if (ldialog != null) return;
        try {
            if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
                return;

            ldialog = new Dialog(MyApplication.currentActivity);
            ldialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            ldialog.setContentView(R.layout.dialog_loder);
            ldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = ldialog.getWindow().getAttributes();
            ldialog.getWindow().setAttributes(wlp);
            ldialog.setCancelable(cancelable);
            ldialog.show();

            ProgressBar progressBar = ldialog.findViewById(R.id.progressBar5);

            progressBar.setIndeterminateDrawable(MyApplication.currentActivity.getResources().getDrawable(ResourceHelper.getResIdFromAttribute(MyApplication.currentActivity, R.drawable.custom_progreesbar_dark)));

        } catch (Exception e) {
            Log.e(TAG, "makeLoader: " + e);
            AvaCrashReporter.send(e, TAG + " ,makeLoader");
        }
    }

    public static void dismiss() {
        try {
            if (ldialog != null)
                if (ldialog.isShowing())
                    ldialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "dismiss: " + e.getMessage());
            AvaCrashReporter.send(e, TAG + " ,dismiss");
        }
        ldialog = null;
    }
}