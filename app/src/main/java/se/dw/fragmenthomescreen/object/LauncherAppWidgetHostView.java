/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.dw.fragmenthomescreen.object;


import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import se.dw.fragmenthomescreen.Config;
import se.dw.fragmenthomescreen.Utilities;

public class LauncherAppWidgetHostView extends AppWidgetHostView {

    @Override
    public boolean equals(Object o) {
        if(o instanceof LauncherAppWidgetHostView){
            return ((LauncherAppWidgetHostView) o).appWidgetId == appWidgetId;
        }
        return false;
    }

    private LayoutInflater mInflater;
	Handler handler;
	View mView;
	public LauncherAppWidgetHostView(Context context) {
		super(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		handler = new Handler();
		mView = this;
		gestureDetector = new GestureDetector(new GestureListener(this));

	}
	String TAG = "WidgetHostView";

    @Override
    public boolean dispatchDragEvent(DragEvent ev){
        boolean r = super.dispatchDragEvent(ev);
        if (r && (ev.getAction() == DragEvent.ACTION_DRAG_STARTED
                || ev.getAction() == DragEvent.ACTION_DRAG_ENDED)){
            // If we got a start or end and the return value is true, our
            // onDragEvent wasn't called by ViewGroup.dispatchDragEvent
            // So we do it here.
            onDragEvent(ev);
        }
        return r;
    }

	public boolean isScrolling = false;
	boolean longpressing = false;
	Runnable isScrollingRunnable = new Runnable() {
		@Override
		public void run () {
			isScrolling = false;
		}
	};

    int appWidgetId = -1;
    public void setAppwidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		View view = null;

		public GestureListener(View view){
			this.view = view;
		}

		@Override
		public void onLongPress (MotionEvent e) {

			//if(listener != null && longpressing){
			//	listener.onLongpress(view, (int)e.getX(), (int)e.getY());
			//}
			//handler.removeCallbacks(longPressRunnable);


			//longpressing = false;
		}

		@Override
		public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			if(Math.abs(distanceX) > Config.TOUCH_SCROLL_SLOP*2 || Math.abs(distanceY) > Config.TOUCH_SCROLL_SLOP ){
				isScrolling = true;
				longpressing = false;
				handler.removeCallbacks(longPressRunnable);
				handler.removeCallbacks(isScrollingRunnable);
				handler.postDelayed(isScrollingRunnable, 300);
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			longpressing = false;
			handler.removeCallbacks(longPressRunnable);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onSingleTapUp (MotionEvent e) {
			Log.i(TAG, "onSingleTapUp remove longpress");
			handler.removeCallbacks(longPressRunnable);
			return super.onSingleTapUp(e);
		}


		@Override
		public boolean onDown (MotionEvent e) {
			taptime2 = System.currentTimeMillis();
			longpressing = true;
			gestureDetector.setIsLongpressEnabled(true);
			handler.removeCallbacks(longPressRunnable);
			handler.postDelayed(longPressRunnable, (long) (ViewConfiguration.getLongPressTimeout()*1.1));
			return super.onDown(e);
		}
		@Override
		public boolean onDoubleTap (MotionEvent e) {
			Log.i(TAG, "onDoubleTap");
			gestureDetector.setIsLongpressEnabled(false);
			return true;
		}

	}

	Runnable longPressRunnable = new Runnable() {
		@Override
		public void run () {
			//Log.i(TAG, "longPressRunnable longpressing="+longpressing);
			//Log.i(TAG, "longPressRunnable taptime="+(System.currentTimeMillis()-taptime)+"ms");
			//Log.i(TAG, "longPressRunnable taptime2="+(System.currentTimeMillis()-taptime2)+"ms");
			if(listener != null && longpressing && !isScrolling){
				listener.onLongpress(mView);
			}
		}
	};

	GestureDetector gestureDetector;
	@Override
	protected View getErrorView() {
		TextView tv = new TextView(getContext());
		tv.setText("Error inflating widget.");
		return tv;
	}

	long taptime = 0;
	long taptime2 = 0;

    boolean pressing = false;

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
                pressing = true;
				taptime = System.currentTimeMillis();
				break;
			case MotionEvent.ACTION_CANCEL:
                pressing = false;
				longpressing = false;
				if (!Utilities.pointInView(this, ev.getX(), ev.getY(), Config.TOUCH_SCROLL_SLOP)) {
					//Log.i(TAG, "Action cancel remove longpress" );
					handler.removeCallbacks(longPressRunnable);
				}
				break;
			case MotionEvent.ACTION_UP:
                pressing = false;
				longpressing = false;
				if (System.currentTimeMillis() - taptime > ViewConfiguration.getLongPressTimeout()/2) {
					//Log.i(TAG, "Action up, Too long for a touch" );
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (!Utilities.pointInView(this, ev.getX(), ev.getY(), Config.TOUCH_SCROLL_SLOP)) {
					//Log.i(TAG, "Action move remove longpress" );
					longpressing = false;
					handler.removeCallbacks(longPressRunnable);
				}

				break;
			default:
				break;
		}

		return gestureDetector.onTouchEvent(ev);
	}


	WidgetListener listener;
	public void setCustomWidgetListener(WidgetListener listener){
		this.listener = listener;
	}
	@Override
	public int getDescendantFocusability() {
		return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
	}
}
