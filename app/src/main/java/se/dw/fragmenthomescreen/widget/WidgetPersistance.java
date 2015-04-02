package se.dw.fragmenthomescreen.widget;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.dw.fragmenthomescreen.App;
import se.dw.fragmenthomescreen.object.ItemInfo;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetInfo;

/**
 * Created by dag on 27/12/14.
 */
public class WidgetPersistance {
	static String TAG = "WidgetPersistance";

	/**
	 * Add a widget to the desktop
	 */
	public static void addDesktopAppWidget (int screen, ItemInfo info) {
		Log.i(TAG, "addDesktopAppWidget");
		ArrayList<ItemInfo> list = getWidgets("" + screen);
		list.add(info);
		setWidgets("" + screen, list);
	}

	/**
	 * Remove a widget from the desktop
	 */
	public static void removeDesktopAppWidget (int screen, ItemInfo info) {

		ArrayList<ItemInfo> list = getWidgets("" + screen);
		if ( list.remove(info) ) {
			setWidgets("" + screen, list);
			Log.i(TAG, "removeDesktopAppWidget removed " + info);
		} else {
			Log.i(TAG, "removeDesktopAppWidget could not remove " + info);
		}

	}

	public static void updateDesktopAppWidget (int screen, ItemInfo info) {

		ArrayList<ItemInfo> list = getWidgets("" + screen);
		int index = list.indexOf(info);
		if ( index != -1 ) {
			list.remove(info);
			list.add(index, info);
			setWidgets("" + screen, list);
			Log.i(TAG, "updateDesktopAppWidget updated widget.");
		} else {
			Log.wtf(TAG, "updateDesktopAppWidget could not find widget to update..!");
		}
	}

	//PERSISTANCE
	public static ArrayList<ItemInfo> getWidgets (String id) {
		String key = "widgetids-" + id;
		ArrayList<ItemInfo> widgets = new ArrayList<>();
		if ( App.get().getSharedPreferences().contains(key) ) {
			try {
				JSONArray arr = new JSONArray(App.get().getSharedPreferences().getString(key, null));
				for ( int i = 0; i < arr.length(); i++ ) {
					JSONObject widget = new JSONObject(arr.getString(i));

					LauncherAppWidgetInfo info = new LauncherAppWidgetInfo(widget.getInt("widgetid"));
					info.height = widget.optInt("height", -1);
					info.width = widget.optInt("width", -1);
					info.x = widget.optInt("x", -1);
					info.y = widget.optInt("y", -1);
					info.screen = widget.optInt("screen", -1);
					info.rowsused = widget.optInt("rowsused", 1);
					info.resizeable = widget.optBoolean("resizeable", false);
					info.resizeableHorizontal = widget.optBoolean("resizeableH", false);
					widgets.add(info);


				}
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}

		return widgets;
	}

	public static void setWidgets (String id, ArrayList<ItemInfo> widgets) {
		//mDesktopAppWidgets = in;
		String key = "widgetids-" + id;

		if ( widgets.size() == 0 ) {
			App.get().getSharedPreferences().edit().remove(key).commit();
		} else {
			try {
				JSONArray arr = new JSONArray();
				for ( int i = 0; i < widgets.size(); i++ ) {
					JSONObject widget = new JSONObject();

					widget.put("type", "widget");
					widget.put("widgetid", widgets.get(i).appWidgetId);

					widget.put("height", widgets.get(i).height);
					widget.put("width", widgets.get(i).width);
					widget.put("x", widgets.get(i).x);
					widget.put("y", widgets.get(i).y);
					widget.put("screen", widgets.get(i).screen);
					widget.put("rowsused", widgets.get(i).rowsused);
					widget.put("resizeable", widgets.get(i).resizeable);
					widget.put("resizeableH", widgets.get(i).resizeableHorizontal);


					arr.put(widget.toString());


					//Log.w("widget"+i, "SET "+widget.toString());
				}
				App.get().getSharedPreferences().edit().putString(key, arr.toString()).commit();
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}
	}

}
