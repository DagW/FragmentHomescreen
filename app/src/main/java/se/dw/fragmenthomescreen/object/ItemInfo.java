/*
 * Copyright (C) 2008 The Android Open Source Project
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
 * Represents an item in the launcher.
 */
public abstract class ItemInfo {

    /**
     * View that holds this widget after it's been created.  This view isn't created
     * until Launcher knows it's needed.
     */
    protected View hostView = null;
    public boolean resizeableHorizontal = false;

    public abstract View getHostView();

    public abstract void setHostView( View view );

    public int appWidgetId;
    public int height = -1;
    public int width = -1;
    public int x = -1;
    public int y = -1;
    public int rowsused = -1;
    public boolean resizeable = false;

    public static final int NO_ID = -1;

    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;

    /**

     */
    public int itemType;

    /**
     * The id of the container that holds this item. For the desktop, this will be
     * will be {@link #NO_ID} (since it is not stored in the settings DB). For user folders
     * it will be the id of the folder.
     */
    public long container = NO_ID;

    /**
     * Iindicates the screen in which the shortcut appears.
     */
    public int screen = -1;

    /**
     * Indicates the X position of the associated cell.
     */
    public int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    public int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates whether the item is a gesture.
     */
    public boolean isGesture = false;

    public ItemInfo () {
    }

    public ItemInfo (ItemInfo info) {
        id = info.id;
        cellX = info.cellX;
        cellY = info.cellY;
        spanX = info.spanX;
        spanY = info.spanY;
        screen = info.screen;
        itemType = info.itemType;
        container = info.container;
    }

    public String getType() {
        if(this instanceof LauncherAppWidgetInfo)
            return "Widget";
        else
            return "ItemInfo";

    }
}
