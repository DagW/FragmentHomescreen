package se.dw.fragmenthomescreen.widget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.dw.fragmenthomescreen.App;
import se.dw.fragmenthomescreen.MainActivity;
import se.dw.fragmenthomescreen.object.ItemInfo;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetInfo;

/**
 * Created by dag on 02/04/15.
 */

public class WidgetFragment extends Fragment {


    int screen = 0;

    ScrollView scrollContainer;
    WidgetContainer widgetContainer;
    Handler handler;
    boolean widgetsAdded = false;
    BindWidgetTask bindTask = null;
    String TAG = "WidgetFragment";
    BinderCallback callback = null;

    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (widgetsAdded == false) {
                widgetsAdded = true;
                if (widgetContainer != null && widgetContainer.getChildCount() == 0) {
                    bindDesktopItems(WidgetPersistance.getWidgets("" + screen), false, null);
                }
            }
        }
    };

    public static WidgetFragment newInstance(int screen) {
        WidgetFragment frag = new WidgetFragment();
        frag.screen = screen;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scrollContainer = new ScrollView(getActivity());
        widgetContainer = new WidgetContainer(getActivity());
        widgetContainer.setScreen(screen);
        handler = new Handler();

        scrollContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        scrollContainer.addView(widgetContainer);

        return scrollContainer;
    }

    public ItemInfo attachWidget(ItemInfo info) {

        if (info != null) {
            widgetContainer.addItemInfo(info);
        } else {
            Log.w(TAG, "Could not find space for item");
        }

        return info;
    }

    public void removeItemInfo(ItemInfo info) {
        widgetContainer.removeItemInfo(info);
    }

    private void bindDesktopItems(ArrayList<ItemInfo> appWidgets, boolean isRefresh, BinderCallback callback) {
        if (appWidgets == null) {
            return;
        }

        MainActivity main = (MainActivity) getActivity();
        if (main == null) {
            Log.wtf(TAG, "bindAppWidgets ERROR.. main is null");
            return;
        }

        if (bindTask == null) {
            this.callback = callback;
            bindTask = new BindWidgetTask(main, isRefresh);
            Log.d(TAG, "bindAppWidgets for screen=" + screen + " starting.. " + appWidgets.size() + "widgets");
            if (Build.VERSION.SDK_INT >= 11) {
                bindTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, appWidgets);
            } else {
                bindTask.execute(appWidgets);
            }
        }
    }

    public WidgetContainer getWidgetContainer() {
        return widgetContainer;
    }

    public int getScreen() {
        return screen;
    }

    /**
     * checkToAddWidget
     * At the creation of WidgetFragment,
     * add a default widget so that the homescreen is not empty
     * shows how to add widgets programmatically
     */
    private void checkToAddWidget() {
        if (getActivity() == null) {
            return;
        }
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(App.get());
        if (!sharedPrefs.contains("widgetpage-" + screen + "-checkToAddWidget")) {

            sharedPrefs.edit().putBoolean("widgetpage-" + screen + "-checkToAddWidget", true).commit();

            MainActivity m = (MainActivity) getActivity();
            AppWidgetManager manager = m.getAppWidgetManager();
            AppWidgetHost host = m.getAppWidgetHost();

            List<AppWidgetProviderInfo> widgetList = manager.getInstalledProviders();

            AppWidgetProviderInfo searchProvider = null;
            AppWidgetProviderInfo searchProvider2 = null;
            AppWidgetProviderInfo clockProvider = null;

            for (AppWidgetProviderInfo info : widgetList) {
                Log.d(TAG, info.provider.getPackageName() + " " + info.provider.getClassName());
                if (info.provider.getClassName().equals("com.google.android.googlequicksearchbox.SearchWidgetProvider")) {
                    searchProvider = info;
                    break;
                }
                if (info.provider.getClassName().equals("com.android.alarmclock.AnalogAppWidgetProvider")) {
                    clockProvider = info;
                }
                if (info.provider.getClassName().equals("com.android.alarmclock.DigitalAppWidgetProvider")) {
                    clockProvider = info;
                }
                if (info.provider.getClassName().equals("com.android.quicksearchbox.SearchWidgetProvider")) {
                    searchProvider2 = info;
                }
            }
            if (searchProvider != null || searchProvider2 != null || clockProvider != null) {
                AppWidgetProviderInfo provider = null;
                if (searchProvider != null) {
                    provider = searchProvider;
                } else if (clockProvider != null) {
                    provider = clockProvider;
                } else {
                    provider = searchProvider2;
                }

                addProvider(m, host, manager, provider);
            }
        }

    }

    public void addProvider(MainActivity m, AppWidgetHost host, AppWidgetManager manager, AppWidgetProviderInfo provider) {
        int id = host.allocateAppWidgetId();
        boolean success;
        success = manager.bindAppWidgetIdIfAllowed(id, provider.provider);

        if (success) {
            AppWidgetHostView hostView = host.createView(getActivity(), id, provider);
            AppWidgetProviderInfo appWidgetInfo = manager.getAppWidgetInfo(id);

            LauncherAppWidgetInfo info = new LauncherAppWidgetInfo(id);
            info.setHostView(hostView);
            info.getHostView().setAppWidget(id, appWidgetInfo);

            ItemInfo launcherInfo = attachWidget(info);
            if (launcherInfo != null) {
                WidgetPersistance.addDesktopAppWidget(screen, launcherInfo);
            }

        } else {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider);
            m.startActivityForResult(intent, MainActivity.REQUEST_BIND_APPWIDGET);
        }

    }

    public interface BinderCallback {
        public void onStarted();

        public void onDone();

        public void aboutToAdd(ItemInfo info);

        public void added(ItemInfo info);
    }

    class BindWidgetTask extends AsyncTask<ArrayList<ItemInfo>, ItemInfo, LinkedList<ItemInfo>> {
        MainActivity main = null;
        boolean isRefresh = false;
        private LinkedList<ItemInfo> mItemInfos;

        public BindWidgetTask(MainActivity main, boolean isRefresh) {
            this.main = main;
            this.isRefresh = isRefresh;
        }

        @Override
        protected void onPostExecute(LinkedList<ItemInfo> s) {
            if (!isRefresh) widgetContainer.removeAllViews();

            for (ItemInfo i : s) {
                if (callback != null) callback.aboutToAdd(i);
                addItem(i);
                if (callback != null) callback.added(i);
            }

            widgetContainer.isAdding(false);
            if (callback != null) callback.onDone();
            bindTask = null;
            callback = null;
        }

        @Override
        protected void onPreExecute() {
            if (callback != null) callback.onStarted();

            widgetContainer.isAdding(true);
        }

        protected void addItem(ItemInfo item) {

            if (item instanceof LauncherAppWidgetInfo) {

                LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) item;
                final int appWidgetId = info.appWidgetId;
                final AppWidgetProviderInfo appWidgetInfo = main.getAppWidgetManager().getAppWidgetInfo(appWidgetId);
                info.setHostView(main.getAppWidgetHost().createView(getActivity(), appWidgetId, appWidgetInfo));
                info.getHostView().setAppWidget(appWidgetId, appWidgetInfo);
                info.getHostView().setTag(item);
                item = info;
            }
            attachWidget(item);
        }

        @Override
        protected void onProgressUpdate(ItemInfo... values) {
            addItem(values[0]);
        }

        @Override
        protected LinkedList<ItemInfo> doInBackground(ArrayList<ItemInfo>... params) {
            ArrayList<ItemInfo> appWidgets = params[0];

            // Sort widgets so active workspace is bound first
            mItemInfos = new LinkedList<>();

            for (ItemInfo info : appWidgets) {
                mItemInfos.add(info);
            }

            return mItemInfos;
        }
    }
}