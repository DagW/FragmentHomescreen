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

import android.view.View;

/**
 * Represents a widget, which just contains an identifier.
 */
public class LauncherAppWidgetInfo extends ItemInfo {



    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    //public AppWidgetHostView hostView = null;
    public LauncherAppWidgetHostView getHostView(){
        if(this.hostView != null && this.hostView instanceof LauncherAppWidgetHostView){
            ((LauncherAppWidgetHostView)this.hostView).setAppwidgetId(appWidgetId);
        }
	    return (LauncherAppWidgetHostView) hostView;
    }
	@Override
	public void setHostView (View view) {
		this.hostView = view;
        if(this.hostView != null && this.hostView instanceof LauncherAppWidgetHostView){
            ((LauncherAppWidgetHostView)this.hostView).setAppwidgetId(appWidgetId);
        }
	}

	public LauncherAppWidgetInfo(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }


    @Override
    public String toString() {
        return Integer.toString(appWidgetId);
    }

	@Override
	public boolean equals (Object o) {
		if(o instanceof LauncherAppWidgetInfo){
			if(((LauncherAppWidgetInfo) o).appWidgetId == this.appWidgetId){
				return true;
			}

		}
		return false;
	}
}
