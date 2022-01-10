package ir.team_x.cloud_transport.taxi_driver.utils;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;

import ir.team_x.cloud_transport.taxi_driver.app.MyApplication;
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter;

/***********************************************
 * Created by AmirReza Erfanian at 23 jun 2019 *
 *                                             *
 ***********************************************/

public class FragmentHelper {

    private static final String TAG = FragmentHelper.class.getSimpleName();
    private static FragmentHelper instance;
    private androidx.fragment.app.Fragment fragmentX = null;
    private Fragment fragment = null;
    private String flag = null;
    private static Activity activity = null;
    private boolean addToBackStack = true;
    private boolean darkMode = true;
    private FragmentManager fragmentManager = null;
    private Bundle bundle = null;
    private int enterAnim = 0;
    private int exitAnim = 0;
    private int popEnterAnim = 0;
    private int popExitAnim = 0;
    private int statusBarColor = -1;
    private int navigationBarColor = -1;

    private androidx.fragment.app.FragmentManager fragmentManagerX = null;
    private @IdRes
    int frame = android.R.id.content;

    /**
     * use android.app.fragment library
     * flag parameter and fragmentManger is create in method
     *
     * @param fragment
     * @return instance of FragmentHelper Class
     */
    public static FragmentHelper toFragment(Activity activity, Fragment fragment) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.flag = fragment.getClass().getSimpleName();
        instance.fragment = fragment;
        instance.fragmentManager = instance.activity.getFragmentManager();
        return instance;
    }

    /**
     * use androidx.fragment.app.Fragment library
     * flag parameter and fragmentManger is create in method
     *
     * @param fragmentX
     * @return instance of FragmentHelper Class
     */
    public static FragmentHelper toFragment(Activity activity, androidx.fragment.app.Fragment fragmentX) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.flag = fragmentX.getClass().getSimpleName();
        instance.fragmentX = fragmentX;
        instance.fragmentManagerX = ((FragmentActivity) instance.activity).getSupportFragmentManager();
        return instance;
    }

    private String getFlag() {
        return instance.flag;
    }

    private int getFrame() {
        return instance.frame;
    }

    private boolean isAddToBackStack() {
        return instance.addToBackStack;
    }

    public FragmentHelper setDarkMode(boolean darkMode) {
        instance.darkMode = darkMode;
        return instance;
    }

    /**
     * if you are like to use animation for input fragment and output fragment
     * you must set TRUE this parameter
     *
     * @return instance of FragmentHelper
     */
    public FragmentHelper setUseAnimation(@AnimRes int enterAnim, @AnimRes int exitAnim, @AnimRes int popEnterAnim, @AnimRes int popExitAnim) {
        instance.enterAnim = enterAnim;
        instance.exitAnim = exitAnim;
        instance.popEnterAnim = popEnterAnim;
        instance.popExitAnim = popExitAnim;
        return instance;
    }

    public FragmentHelper setUseAnimation(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        instance.enterAnim = enterAnim;
        instance.exitAnim = exitAnim;
        return instance;
    }

    /**
     * if you set TRUE this parameter
     * fragment don't add to back stack and
     * back press button don't work
     *
     * @param addToBackStack default value is true
     * @return
     */
    public FragmentHelper setAddToBackStack(boolean addToBackStack) {
        instance.addToBackStack = addToBackStack;
        return instance;
    }

    public FragmentHelper setArguments(Bundle bundle) {
        if (instance.fragment != null) {
            instance.fragment.setArguments(bundle);
        }

        if (instance.fragmentX != null) {
            instance.fragmentX.setArguments(bundle);
        }
        return instance;
    }

    /**
     * if you want use another container ,you must create frameLayout in your activity view and
     * send id of view to this function
     *
     * @param frame use default Container android
     * @return
     */
    public FragmentHelper setFrame(int frame) {
        instance.frame = frame;
        return instance;
    }

    /**
     * if you use this function fragment only add to stack and don't remove last visible fragment
     */
    public void add() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (instance.fragment != null) {
                        FragmentTransaction fragmentTransaction = instance.fragmentManager.beginTransaction();
                        if (isAddToBackStack()) {
                            fragmentTransaction.addToBackStack(null);
                        }
                        if (instance.fragment.isAdded()) {
                            return;
                        }
                        fragmentTransaction.add(instance.frame, instance.fragment, instance.flag);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else if (instance.fragmentX != null) {
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = instance.fragmentManagerX.beginTransaction();
                        if (isAddToBackStack()) {
                            fragmentTransaction.addToBackStack(null);
                        }
                        fragmentTransaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                        if (instance.fragmentX.isAdded()) {
                            return;
                        }
                        fragmentTransaction.add(instance.frame, instance.fragmentX, instance.flag);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else {
                        Log.e(TAG, "can't add " + flag + " to " + frame);
                    }

                }
            }, 100);
            setWindowStyle();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "FragmentHelper class, add method");
            Log.e(TAG, "can't add " + flag + " to " + frame);
        }

    }

    /**
     * remove last visible fragment and replace new fragment with it
     */
    public void replace() {
        try {
            KeyBoardHelper.hideKeyboard();
            MyApplication.handler.postDelayed(() -> {
                if (instance.fragment != null) {
                    FragmentTransaction fragmentTransaction = instance.fragmentManager.beginTransaction();
                    if (isAddToBackStack()) {
                        fragmentTransaction.addToBackStack(null);
                    }
                    fragmentTransaction.replace(instance.frame, instance.fragment, instance.flag);
                    fragmentTransaction.commitAllowingStateLoss();
                } else if (instance.fragmentX != null) {
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = instance.fragmentManagerX.beginTransaction();
                    if (isAddToBackStack()) {
                        fragmentTransaction.addToBackStack(null);
                    }
                    fragmentTransaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                    fragmentTransaction.replace(instance.frame, instance.fragmentX, instance.flag);
                    fragmentTransaction.commitAllowingStateLoss();
                } else {
                    Log.e(TAG, "can't replace " + flag + " to " + frame);
                }
            }, 100);
            setWindowStyle();
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "FragmentHelper class, replace method");
            Log.e(TAG, "can't replace " + flag + " to " + frame);
        }

    }

    public static FragmentHelper taskFragment(Activity activity, String tag) {
        instance = new FragmentHelper();
        instance.activity = activity;
        instance.fragmentManagerX = ((FragmentActivity) instance.activity).getSupportFragmentManager();
        instance.fragmentManager = instance.activity.getFragmentManager();
        instance.fragment = instance.fragmentManager.findFragmentByTag(tag);
        instance.fragmentX = instance.fragmentManagerX.findFragmentByTag(tag);
        return instance;
    }

    public boolean isVisible() {
        if (instance.fragment != null) {
            if (instance.fragment.isVisible()) {
                return true;
            }
        }
        if (instance.fragmentX != null) {
            if (instance.fragmentX.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void remove() {
        try {
            if (instance.fragment != null) {
                Log.i(TAG, "remove: is null");
                FragmentTransaction transaction = instance.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                transaction.remove(instance.fragment).commit();
                instance.fragmentManager.popBackStack();
            }

            if (instance.fragmentX != null) {
                androidx.fragment.app.FragmentTransaction transactionX = instance.fragmentManagerX.beginTransaction();
                transactionX.setCustomAnimations(instance.enterAnim, instance.exitAnim, instance.popEnterAnim, instance.popExitAnim);
                transactionX.remove(fragmentX).commit();
                instance.fragmentManagerX.popBackStack();
            }
        } catch (Exception e) {
            AvaCrashReporter.send(e, "FragmentHelper class, remove method");
            e.printStackTrace();
        }

    }

    public FragmentHelper setNavigationBarColor(@ColorInt int color) {
        instance.navigationBarColor = color;
        return instance;
    }

    public FragmentHelper setStatusBarColor(@ColorInt int color) {
        instance.statusBarColor = color;
        return instance;
    }

    private void setWindowStyle() {
        try {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (darkMode)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                if (statusBarColor != -1) {
                    window.setStatusBarColor(statusBarColor);
                }
                if (navigationBarColor != -1) {
                    window.setNavigationBarColor(navigationBarColor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvaCrashReporter.send(e, "FragmentHelper class, setWindowStyle method");
        }
    }

}
