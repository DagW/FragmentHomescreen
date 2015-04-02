package se.dw.fragmenthomescreen;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dag on 02/04/15.
 */
public class App extends Application {

	static App instance = null;

	@Override
	public void onCreate () {
		super.onCreate();
		instance = this;
	}

	public static App get(){
		return instance;
	}

	public SharedPreferences getSharedPreferences(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(App.get());
		return sharedPrefs;
	}
}
