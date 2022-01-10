package ir.team_x.cloud_transport.taxi_driver.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogErrorBinding;
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter;
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil;

public class ErrorDialog {
    static Dialog dialog;
    DialogErrorBinding binding;
    private Runnable closeRunnable;
    private Runnable tryAgainRunnable;
    private Runnable bodyRunnable;
    private String closeText = "بستن";
    private String tryAgainText = "تلاش مجدد";
    private String messageText;
    private String titleText = null;
    private boolean cancelable;
    private Runnable dismissRunnable;

    public ErrorDialog closeBtnRunnable(String closeText, Runnable closeRunnable) {
        this.closeRunnable = closeRunnable;
        this.closeText = closeText;
        return this;
    }

    public ErrorDialog tryAgainBtnRunnable(String tryAgainText, Runnable tryAgainRunnable) {
        this.tryAgainRunnable = tryAgainRunnable;
        this.tryAgainText = tryAgainText;
        return this;
    }

    public ErrorDialog bodyRunnable(Runnable bodyRunnable) {
        this.bodyRunnable = bodyRunnable;
        return this;
    }

    public ErrorDialog messageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public ErrorDialog titleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public ErrorDialog cancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public ErrorDialog onDismiss(Runnable runnable) {
        this.dismissRunnable = runnable;
        return this;
    }

    public void show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing())
            return;
        dialog = new Dialog(MyApplication.currentActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.ExpandAnimation;
        binding = DialogErrorBinding.inflate(LayoutInflater.from(MyApplication.context));
        dialog.setContentView(binding.getRoot());
        TypeFaceUtil.Companion.overrideFont(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
//    wlp.windowAnimations = R.style.ExpandAnimation;
        dialog.getWindow().setAttributes(wlp);

        dialog.setCancelable(true);

        binding.imgClose.setOnClickListener(v -> {
            if (closeRunnable != null)
                closeRunnable.run();
            else
                dismiss();
        });

        binding.imgTryAgain.setOnClickListener(v -> {
            if (tryAgainRunnable != null)
                tryAgainRunnable.run();
            dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            if (dismissRunnable != null)
                dismissRunnable.run();
        });

        dialog.show();
    }

    public static void dismiss() {
        try {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        } catch (Exception e) {
            AvaCrashReporter.send(e, "ErrorDialog class, dismiss method");
        }
        dialog = null;
    }

}
