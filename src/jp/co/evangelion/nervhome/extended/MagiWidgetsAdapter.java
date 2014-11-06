package jp.co.evangelion.nervhome.extended;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.co.evangelion.nervhome.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MagiWidgetsAdapter extends BaseAdapter {
	private static String[] Fpa = null;
	private LayoutInflater fpb;
	private PackageManager fpc;
	private List<AppWidgetProviderInfo> fpd;
	private Rect fpe = new Rect(0, 0, 96, 96);

	public MagiWidgetsAdapter(Context context, AppWidgetManager appWidgetManager) {
		if (Fpa == null) {
			Fpa = context.getResources().getStringArray(
					R.array.maagi_widgets_values);
		}
		fpb = LayoutInflater.from(context);
		fpc = context.getPackageManager();
		fpd = Mpa(appWidgetManager);
	}

	static List<AppWidgetProviderInfo> Mpa(AppWidgetManager appWidgetManager) {
		List<AppWidgetProviderInfo> list = appWidgetManager
				.getInstalledProviders();
		int count = list.size();
		for (int i = 0; i < count;) {
			String name = list.get(i).provider.getPackageName();
			boolean found = false;
			if (Fpa != null && name != null) {
				for (int j = 0; j < Fpa.length; j++) {
					if (Fpa[j].equals(name)) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				list.remove(i);
				count--;
			} else {
				i++;
			}
		}

		class AppWidgetProviderInfoComparator implements
				Comparator<AppWidgetProviderInfo> {
			private Collator fpa;

			private AppWidgetProviderInfoComparator() {
				fpa = Collator.getInstance();
			}

			@Override
			public int compare(AppWidgetProviderInfo lhs,
					AppWidgetProviderInfo rhs) {
				return fpa.compare(lhs.label, rhs.label);
			}
		}
		Collections.sort(list, new AppWidgetProviderInfoComparator());
		return list;
	}

	@Override
	public int getCount() {
		if (fpd == null)
			return 0;
		return fpd.size();
	}

	@Override
	public Object getItem(int arg0) {
		if (fpd == null)
			return null;
		return fpd.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if(arg1 == null)
			arg1 = fpb.inflate(R.layout.pick_item, null);
		AppWidgetProviderInfo appwidgetproviderinfo = fpd.get(arg0);
		TextView textView = (TextView) arg1;
		textView.setText(appwidgetproviderinfo.label);
		if (appwidgetproviderinfo.icon != 0){
			Drawable drawable = fpc.getDrawable(appwidgetproviderinfo.provider.getPackageName(), appwidgetproviderinfo.icon, null);
			drawable.setBounds(fpe);
			textView.setCompoundDrawables(drawable, null, null, null);
		}
		return arg1;
	}

	public Intent mPa(int which) {
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		intent.setComponent(fpd.get(which).provider);
		return intent;
	}
}
