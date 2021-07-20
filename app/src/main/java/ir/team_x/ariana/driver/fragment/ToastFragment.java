package ir.team_x.ariana.driver.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.team_x.ariana.driver.R;
import ir.team_x.ariana.driver.app.MyApplication;
import ir.team_x.ariana.driver.databinding.FreeToastBinding;
import ir.team_x.ariana.driver.utils.FragmentHelper;
import ir.team_x.ariana.operator.utils.TypeFaceUtil;


public class ToastFragment extends Fragment {

    public static final String TAG = ToastFragment.class.getSimpleName();
    FreeToastBinding binding;
    View view;
    float startPoint, endPoint, lastPoint;

    private int getSreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        MyApplication.currentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return screenWidth;
    }

    private int getSreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        MyApplication.currentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        return screenHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FreeToastBinding.inflate(inflater);
        TypeFaceUtil.Companion.overrideFont(binding.getRoot());

        MyApplication.handler.postDelayed(() -> remove(), 8000);

        binding.getService.setOnClickListener(view -> {
            remove();
            FragmentHelper.toFragment(MyApplication.currentActivity, new FreeLoadsFragment())
                    .setAddToBackStack(true)
                    .replace();
        });

        binding.toastMover.setOnTouchListener((view, event) -> {
            float AX = event.getAxisValue(MotionEvent.AXIS_X);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //save start point
                startPoint = AX;
                lastPoint = AX;
            } else {
                //for after start to end touch move layout
                binding.toastLayoutRoot.setTranslationX(AX - startPoint);
                binding.toastLayoutRoot.setAlpha((1 - Math.abs((AX - startPoint) / getSreenWidth())) / 2);
                //update last position touch
                lastPoint = AX;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {

                //بررسی میکنه اگر بیشتر از پنجاه درصد لایه از صفحه بیرون بود از صفحه میندازه بیرون وگرنه برمیگرده سرجاش
                if (Math.abs(binding.toastLayoutRoot.getTranslationX()) < getSreenWidth() / 3.5) {
                    //return to default
                    endPoint = AX;
                    binding.toastLayoutRoot.animate().translationX(0).alpha(1).setDuration(500).start();
                } else {
                    //remove from page
                    float x = binding.toastLayoutRoot.getTranslationX() + getSreenWidth();
                    float y = getSreenWidth() * 2;
                    if (x < getSreenWidth()) {
                        y *= -1;
                    }
                    binding.toastLayoutRoot.animate().translationX(y).setDuration(500).start();
                    MyApplication.handler.postDelayed(() -> remove(), 500);
                }

            }
            return true;
        });


        String message = getArguments().getString("msg");
        binding.txtMessage.setText(message);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void remove() {
        try {
            if (getFragmentManager() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = MyApplication.currentActivity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setNavigationBarColor(getResources().getColor(R.color.colorBlack));
                }

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down);

                Fragment currentFrag = getFragmentManager().findFragmentById(R.id.toast_container);
                transaction.remove(currentFrag).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Fragment fragment = null;

    public void addToast(String message, Activity activity) {
        try {
            if (fragment != null)
                if (fragment.isVisible()) return;
            fragment = new ToastFragment();
            Bundle bundle = new Bundle();
            bundle.putString("msg", message);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = MyApplication.currentActivity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setNavigationBarColor(MyApplication.currentActivity.getResources().getColor(R.color.colorPink));
            }

            fragment.setArguments(bundle);
            FragmentManager fragmentManager = MyApplication.currentActivity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down);
            fragmentTransaction.add(R.id.toast_container,fragment, ToastFragment.TAG);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}




