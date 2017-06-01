package se.dw.fragmenthomescreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import se.dw.fragmenthomescreen.Utilities;
import se.dw.fragmenthomescreen.object.ItemInfo;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetHostView;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetInfo;
import se.dw.fragmenthomescreen.object.WidgetListener;


/**
 * Created by dag on 19/12/14.
 */
public class WidgetContainer extends LinearLayout {


    String TAG = "WidgetContainerLayout";
    boolean isAdding = false;
    private int screen = -1;

    public WidgetContainer(Context context) {
        super(context);
        setup();
    }

    public WidgetContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public WidgetContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setOrientation(LinearLayout.VERTICAL);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (getHeight() > 0) {
                    //If you want to calculate rows etc this is a good place
                }
            }
        });
    }

    @Override
    public int getDescendantFocusability() {
        return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    }

    public void addItemInfo(final ItemInfo inInfo) {
        if (indexOfChild(inInfo.getHostView()) != -1) {
            return;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(inInfo.width, inInfo.height);
        params.setMargins(0, 0, 0, 0);
        View v = inInfo.getHostView();
        try {
            addView(v, params);
        } catch (Exception e) {
            e.printStackTrace();
            addView(v, params);
        }

        if (inInfo instanceof LauncherAppWidgetInfo) {
            ((LauncherAppWidgetHostView) inInfo.getHostView()).setCustomWidgetListener(new WidgetListener() {
                @Override
                public void onLongpress(View view) {
                    removeItemInfo(inInfo);
                    WidgetPersistance.removeDesktopAppWidget(screen, inInfo);
                    Utilities.vibrate(25);
                    Utilities.showToast(getContext(), "Widget removed.");
                }
            });
        }

    }

    public void removeItemInfo(ItemInfo info) {

        if (indexOfChild(info.getHostView()) != -1) {
            removeView(info.getHostView());
        }


    }

    public void isAdding(boolean b) {
        this.isAdding = b;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }
}