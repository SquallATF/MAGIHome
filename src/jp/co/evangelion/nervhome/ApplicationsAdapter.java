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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

/**
 * GridView adapter to show the list of applications and shortcuts
 */
public class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
	private final LayoutInflater mInflater;
	private Drawable mBackground;
	private int mTextColor = 0;
	private boolean useThemeTextColor = false;
	private Typeface themeFont = null;
	// TODO: Check if allItems is used somewhere else!
	public static ArrayList<ApplicationInfo> allItems = new ArrayList<ApplicationInfo>();
	private static HashMap<ApplicationInfo, View> viewCache = new HashMap<ApplicationInfo, View>();
	private CatalogueFilter filter;
	private static final Collator sCollator = Collator.getInstance();
	private boolean mWithDrawingCache = false;

	public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
		super(context, 0, apps);

		mInflater = LayoutInflater.from(context);
	}
	
	public void buildViewCache(ViewGroup parent) {
		for(int i = 0; i < getCount(); i++) {
			final ApplicationInfo info = getItem(i);	
			addToViewCache(parent, info);
		}
	}
	
	private void addToViewCache(ViewGroup parent, ApplicationInfo info) {
		if (!info.filtered) {
			info.icon = Utilities.createIconThumbnail(info.icon, getContext());
			info.filtered = true;
		}
		View convertView = mInflater.inflate(R.layout.application_boxed, parent, false);
		convertView.setDrawingCacheEnabled(mWithDrawingCache);
		viewCache.put(info, convertView);
		final TextView textView = (TextView) convertView;
		textView.setCompoundDrawablesWithIntrinsicBounds(null, info.icon, null, null);
		textView.setText(info.title);
		if (useThemeTextColor) {
			textView.setTextColor(mTextColor);
		}
		//ADW: Custom font
		if(themeFont!=null) textView.setTypeface(themeFont);
		// so i'd better not use it, sorry themers
		if (mBackground != null)
			convertView.setBackground(mBackground);
	}
	
	public void setChildDrawingCacheEnabled(boolean aValue) {
		if (mWithDrawingCache != aValue) {
			mWithDrawingCache = aValue;
			for(View v : viewCache.values()) {
				v.setDrawingCacheEnabled(aValue);
				if(aValue)
				    v.buildDrawingCache();
				else
				    v.destroyDrawingCache();
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ApplicationInfo info = getItem(position);
		if (viewCache.isEmpty())
			buildViewCache(parent);
		if (!viewCache.containsKey(info))
			addToViewCache(parent, info);
		View result = viewCache.get(info);
        //ADW:Counters
        ((CounterTextView)result).setCounter(info.counter, info.counterColor);
		return result; 
	}


	@Override
	public void add(ApplicationInfo info) {
		//check allItems before added. It is a fix for all of the multi-icon issue, but will 
		//lose performance. Anyway, we do not expected to have many applications.
		synchronized (allItems) {
			/*if (!allItems.contains(info)) {
				changed = true;
				allItems.add(info);
				Collections.sort(allItems,new ApplicationInfoComparator());
			}*/
			int count=allItems.size();
			boolean found=false;
			for(int i=0;i<count;i++){
				ApplicationInfo athis=allItems.get(i);
				if(info.intent.getComponent()!=null){
					if(athis.intent.getComponent().flattenToString().equals(
							info.intent.getComponent().flattenToString())){
						found=true;
						break;
					}
				}
			}
			if(!found){
				allItems.add(info);
				Collections.sort(allItems,new ApplicationInfoComparator());
				updateDataSet();
			}
		} 
	}

	//2 super functions, to make sure related add/clear do not affect allItems.
	//in current Froyo/Eclair, it is not necessary.
	void superAdd(ApplicationInfo info) {
		if(info!=null)
			super.add(info);
	}

	void superClear() {
		super.clear();
	}

	@Override
	public void remove(ApplicationInfo info) {
		synchronized (allItems) {
			//allItems.remove(info);
			int count=allItems.size();
			for(int i=0;i<count;i++){
				ApplicationInfo athis=allItems.get(i);
				if(info.intent.getComponent()!=null){
					if(athis.intent.getComponent().flattenToString().equals(
							info.intent.getComponent().flattenToString())){
						viewCache.remove(athis);
						allItems.remove(i);
						Collections.sort(allItems,new ApplicationInfoComparator());
						updateDataSet();
						break;
					}
				}
			}			
		}
	}

	private String getComponentName(ApplicationInfo info) {
		if (info == null || info.intent == null)
			return null;
		ComponentName cmpName = info.intent.getComponent();
		if (cmpName == null)
			return null;
		return cmpName.flattenToString();
	}

	private void filterApps(ArrayList<ApplicationInfo> theFiltered,
			ArrayList<ApplicationInfo> theItems) {
		theFiltered.clear();
		//AppGrpUtils.checkAndInitGrp();
		if (theItems != null) {
			int length = theItems.size();

			for (int i = 0; i < length; i++) {
				ApplicationInfo info = theItems.get(i);
				String s = getComponentName(info);

				if (s != null) {
					theFiltered.add(info);
				}
			}
		}
	}
	//filter,sort,update
    public void updateDataSet()
	{
        getFilter().filter(null);
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new CatalogueFilter();
		return filter;
	}


	private class CatalogueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults result = new FilterResults();
                ArrayList<ApplicationInfo> filt = new ArrayList<ApplicationInfo>();

                synchronized (allItems) {
                        filterApps(filt, allItems);
                }

                result.values = filt;
                result.count = filt.size();
                return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                        FilterResults results) {
                // NOTE: this function is *always* called from the UI thread.
                ArrayList<ApplicationInfo> localFiltered =(ArrayList<ApplicationInfo>) results.values;

                setNotifyOnChange(false);
                superClear();
                // there could be a serious sync issue.
                // very bad
                for (int i = 0;i < results.count; i++) {
                    superAdd(localFiltered.get(i));
                }

                notifyDataSetChanged();

        }
	}
    static class ApplicationInfoComparator implements Comparator<ApplicationInfo> {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
        	if(a.activityIndex < b.activityIndex){
        		return -1;
        	}
        	if (b.activityIndex < a.activityIndex){
        		return 1;
        	}
            return sCollator.compare(a.title.toString(), b.title.toString());
        }
    }
	
	public static void clearAll() {
		synchronized (allItems) {
			allItems.clear();
			viewCache.clear();
		}
	}
}
