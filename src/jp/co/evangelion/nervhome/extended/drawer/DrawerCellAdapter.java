package jp.co.evangelion.nervhome.extended.drawer;

import jp.co.evangelion.nervhome.ApplicationInfo;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DrawerCellAdapter extends BaseAdapter {
	
	private Context fpa;
	private ApplicationInfo[] fpb;
	private int fpc;
	
	public DrawerCellAdapter(Context context, ApplicationInfo[] infos) {
		fpa = context;
		mPa(infos);
	}

	@Override
	public int getCount() {
		return fpc;
	}

	@Override
	public Object getItem(int position) {
		if (fpb != null && position < fpc) {
			return fpb[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DrawerCell drawercell;
		if (convertView == null) {
			drawercell = new DrawerCell(fpa);
			drawercell.setTextSize(13);
			drawercell.setGravity(Gravity.AXIS_SPECIFIED);
			drawercell.setCompoundDrawablePadding(24);
			drawercell.setHeight(223);
			drawercell.setTextColor(Color.WHITE);
			drawercell.setMaxLines(2);
		} else {
			drawercell = (DrawerCell)convertView;
		}
		try {
			ApplicationInfo info = fpb[position];
			drawercell.setText(info.title);
			info.icon.setBounds(4, 24, 100, 120);
			drawercell.setCompoundDrawables(null, info.icon, null, null);
		} catch (Exception e) {
			
		}
		return drawercell;
	}

	public void mPa(ApplicationInfo[] infos) {
		fpb = infos;
		if (fpb != null) {
			fpc = fpb.length;
		} else {
			fpc = 0;
		}
		notifyDataSetChanged();
		
	}

}
