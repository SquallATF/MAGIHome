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

package jp.co.evangelion.nervhome;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Represents a launchable application. An application is made of a name (or title),
 * an intent and an icon.
 */
public class ApplicationInfo extends ItemInfo {
    /**
     * The "unread counter" notification
     */
    public int counter;
    /**
     * The "unread counter" bubble color
     */
    public int counterColor;
    /**
     * The application name.
     */
    public CharSequence title;

    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * The application icon.
     */
    public Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    boolean filtered;

    /**
     * Indicates whether the icon comes from an application's resource (if false)
     * or from a custom Bitmap (if true.)
     */
    boolean customIcon;

    int hashCode=0;
    
    int activityIndex=Integer.MAX_VALUE;
    
    static String[] activityNames = {"jp.co.evangelion.evaplayer/jp.co.evangelion.evaplayer.StartupActivity", "jp.co.evangelion.nervagentapps/jp.co.evangelion.nervagentapps.NervStaffActivity", "jp.co.evangelion.attack_of_8th_angel/com.unity3d.player.UnityPlayerProxyActivity"};
    
    private static int activityCount = activityNames.length;

    /**
     * If isShortcut=true and customIcon=false, this contains a reference to the
     * shortcut icon as an application's resource.
     */
    Intent.ShortcutIconResource iconResource;

    ApplicationInfo() {
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
    }

	public ApplicationInfo(ApplicationInfo info) {
        super(info);
        assignFrom(info);
	}
	
	public static int getNervActivityNameIndex(String activityName){
		if(activityName != null) {
			for(int i = 0; i< activityCount; i++){
				if (activityNames[i].equals(activityName)){
					return i;
				}
			}
		}
		return Integer.MAX_VALUE;
	}

    @Override
	void assignFrom(ItemInfo info) {
    	if (info instanceof ApplicationInfo)
    	{
    		ApplicationInfo nfo = (ApplicationInfo)info;
	        title = nfo.title.toString();
	        intent = new Intent(nfo.intent);
	        if (nfo.iconResource != null) {
	            iconResource = new Intent.ShortcutIconResource();
	            iconResource.packageName = nfo.iconResource.packageName;
	            iconResource.resourceName = nfo.iconResource.resourceName;
	        }
	        icon = nfo.icon;
	        filtered = nfo.filtered;
	        customIcon = nfo.customIcon;
	        counter=nfo.counter;
	        counterColor=nfo.counterColor;
    	}
    }

    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link LauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
    }

    @Override
    void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);

        String titleStr = title != null ? title.toString() : null;
        values.put(LauncherSettings.BaseLauncherColumns.TITLE, titleStr);

        String uri = intent != null ? intent.toUri(0) : null;
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, uri);

        if (customIcon) {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    LauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
            Bitmap bitmap = ((FastBitmapDrawable) icon).getBitmap();
            writeBitmap(values, bitmap);
        } else {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    LauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
            if (iconResource != null) {
                values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE,
                        iconResource.packageName);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE,
                        iconResource.resourceName);
            }
        }
    }

	@Override
	public String toString() {
		return title.toString();
	}

}
