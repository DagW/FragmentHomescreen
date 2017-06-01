package se.dw.fragmenthomescreen;

import android.content.Context;
import android.util.Log;
import android.view.ViewConfiguration;

/**
 * Created by dag on 20/01/15.
 */
public class Config {
    public static int SWIPE_MIN_DISTANCE = 120;
    public static int SWIPE_THRESHOLD_VELOCITY = 250;
    public static int SWIPE_MAX_OFF_PATH = 150;
    public static int TOUCH_SCROLL_SLOP = 5;
    static String TAG = "[CONFIG]";

    /*
    Sets touch configuration - swipe distance etc
    to be scaled to the device screen
     */
    public static void setTouchConfigs(Context cxt) {
        final ViewConfiguration vc = ViewConfiguration.get(cxt);
        SWIPE_MIN_DISTANCE = vc.getScaledPagingTouchSlop() * 3;
        SWIPE_THRESHOLD_VELOCITY = vc.getScaledMinimumFlingVelocity() * 4;
        SWIPE_MAX_OFF_PATH = vc.getScaledTouchSlop() * 10;
        TOUCH_SCROLL_SLOP = (vc.getScaledTouchSlop() / 5) * 2;

        Log.i(TAG, "SWIPE_MIN_DISTANCE=" + SWIPE_MIN_DISTANCE +
                " SWIPE_THRESHOLD_VELOCITY=" + SWIPE_THRESHOLD_VELOCITY +
                " SWIPE_MAX_OFF_PATH=" + SWIPE_MAX_OFF_PATH +
                " TOUCH_SCROLL_SLOP=" + TOUCH_SCROLL_SLOP);
    }

}
