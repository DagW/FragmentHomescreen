package se.dw.fragmenthomescreen;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.dw.fragmenthomescreen.object.ItemInfo;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetHost;
import se.dw.fragmenthomescreen.object.LauncherAppWidgetInfo;
import se.dw.fragmenthomescreen.widget.WidgetFragment;
import se.dw.fragmenthomescreen.widget.WidgetPersistance;


public class MainActivity extends ActionBarActivity {

	public static int REQUEST_BIND_APPWIDGET = 120;
	public static int REQUEST_PICK_APPWIDGET = 121;
	public static int REQUEST_CREATE_APPWIDGET = 122;
	private int APPWIDGET_HOST_ID = 100;


	ViewPager viewPager = null;
	//Fragment currentFragment = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewPager = (ViewPager) findViewById(R.id.pager);

		if ( savedInstanceState == null ) {

			WidgetPagerAdapter adapter = new WidgetPagerAdapter(getSupportFragmentManager());
			viewPager.setAdapter(adapter);
			/*if(currentFragment == null)
				currentFragment = WidgetFragment.newInstance(0);
			getSupportFragmentManager().beginTransaction().add(R.id.container, currentFragment).commit();*/
		}

		Config.setTouchConfigs(this);
	}


	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if ( id == R.id.action_add_widget ) {

			selectWidget();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupWidget () {
		appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
		appWidgetHost = new LauncherAppWidgetHost(MainActivity.this, APPWIDGET_HOST_ID);
	}

	@Override
	public void onStart () {
		super.onStart();
		getAppWidgetHost().startListening();
	}

	@Override
	public void onStop () {
		super.onStop();
		if(appWidgetHost != null)
			getAppWidgetHost().stopListening();
	}


	AppWidgetManager appWidgetManager = null;
	public AppWidgetManager getAppWidgetManager () {
		if ( appWidgetManager == null ) {
			setupWidget();
		}
		return appWidgetManager;
	}

	AppWidgetHost appWidgetHost = null;
	public AppWidgetHost getAppWidgetHost () {
		if ( appWidgetHost == null ) {
			setupWidget();
		}
		return appWidgetHost;
	}

	public void selectWidget () {
		int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
		Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		addEmptyData(pickIntent);
		Log.w("Main", "Allocated widget id = " + appWidgetId + " " + pickIntent);
		startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
	}

	// For some reason you have to add this empty data, else it won't work
	public void addEmptyData (Intent pickIntent) {
		ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<>();
		pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
		ArrayList<Bundle> customExtras = new ArrayList<>();
		pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
	}

	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		Log.i("Main", "onActivityResult(int requestCode=" + requestCode + ", int resultCode=" + resultCode + ", Intent data=" + data + ")");
		if ( resultCode == Activity.RESULT_OK ) {
			if ( requestCode == REQUEST_PICK_APPWIDGET ) {
				configureWidget(data);
			} else if ( requestCode == REQUEST_CREATE_APPWIDGET ) {
				createWidget(data);
			} else if ( requestCode == REQUEST_BIND_APPWIDGET ) {
				createWidget(data);
			}
		} else if ( resultCode == Activity.RESULT_CANCELED && data != null ) {
			int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if ( appWidgetId != -1 ) {
				getAppWidgetHost().deleteAppWidgetId(appWidgetId);
			}
		}
	}

	// Show configuration activity of the widget picked by the user
	private void configureWidget (Intent data) {
		Bundle extras = data.getExtras();
		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidgetInfo = getAppWidgetManager().getAppWidgetInfo(appWidgetId);
		if ( appWidgetInfo.configure != null ) {
			Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidgetInfo.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
		} else {
			createWidget(data);
		}
	}

	// Get an instance of the selected widget as a AppWidgetHostView
	public void createWidget (Intent data) {

		if ( data == null ) return;

		Bundle extras = data.getExtras();

		int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidgetInfo = getAppWidgetManager().getAppWidgetInfo(appWidgetId);

		// Build Launcher-specific widget info and save
		LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId);
		launcherInfo.setHostView(getAppWidgetHost().createView(MainActivity.this, appWidgetId, appWidgetInfo));
		launcherInfo.getHostView().setAppWidget(appWidgetId, appWidgetInfo);

		WidgetFragment fragment = getCurrentWidgetFragment();
		if ( fragment != null ) {
			ItemInfo info = fragment.attachWidget(launcherInfo);
			if ( info != null ) {
				WidgetPersistance.addDesktopAppWidget(fragment.getScreen(), info);
			}
			getAppWidgetHost().startListening();
		}
	}

	public WidgetFragment getCurrentWidgetFragment () {
		int index = viewPager.getCurrentItem();
		WidgetPagerAdapter adapter = ((WidgetPagerAdapter)viewPager.getAdapter());
		WidgetFragment fragment = adapter.getFragment(index);
		return fragment;
	}

	/**
	 * A simple pager adapter that represents 5 WidgetFragment objects, in
	 * sequence.
	 */
	private class WidgetPagerAdapter extends FragmentStatePagerAdapter {
		private Map<Integer, WidgetFragment> mPageReferenceMap = new HashMap<>();

		public WidgetPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		public WidgetFragment getFragment(int key) {
			return mPageReferenceMap.get(key);
		}
		@Override
		public android.support.v4.app.Fragment getItem (int position) {
			WidgetFragment fragment = WidgetFragment.newInstance(position);
			mPageReferenceMap.put(position, fragment);
			return fragment;
		}
		public void destroyItem(View container, int position, Object object) {
			super.destroyItem(container, position, object);
			mPageReferenceMap.remove(position);
		}
		@Override
		public int getCount() {
			return 5;
		}
	}
}
