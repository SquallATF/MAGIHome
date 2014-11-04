package jp.co.evangelion.nervhome.extended.drawer;

import java.util.ArrayList;
import java.util.List;

import jp.co.evangelion.nervhome.ApplicationInfo;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class ViewPagerAdapter extends PagerAdapter {
	
	private Context fpa;
	private List<ApplicationInfo> fpb;
	private OnItemClickListener fpc;
	private OnItemLongClickListener fpd;
	private int fpe;
	private List<DrawerCellAdapter> fpf = new ArrayList<DrawerCellAdapter>();
	
	public ViewPagerAdapter(Context context, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener){
		fpa = context;
		fpc = onItemClickListener;
		fpd = onItemLongClickListener;
	}

	@Override
	public int getCount() {
		return fpe;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final ViewPager viewpager = (ViewPager)container;
		GridView gridview = new GridView(fpa);
		gridview.setOnItemClickListener(fpc);
		gridview.setOnItemLongClickListener(fpd);
		gridview.setNumColumns(4);
		int count = fpb.size();
		int start = position * 20;
		int len = count - start;
		final ApplicationInfo[] infos = fpb.subList(start, len > 20 ? start + 20 : start + len)
				.toArray(new ApplicationInfo[0]);
		DrawerCellAdapter adapter = new DrawerCellAdapter(fpa, infos);
		gridview.setAdapter(adapter);
		fpf.add(adapter);
		viewpager.addView(gridview, 0);
		return gridview;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		final ViewPager viewpager = (ViewPager)container;
		viewpager.removeView((View)object);
		fpf.remove(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	public void mPa(List<ApplicationInfo> list) {
		fpb = list;
		int j;
		if (fpb == null) {
			fpe = 0;
			j = 0;
		} else {
			int size = fpb.size();
			if (size % 20 == 0) {
				fpe = size / 20;
				j = size;
			} else {
				fpe = size / 20 + 1;
				j = size;
			}
		}
		int k = fpf.size();		
		for (int l = 0;l < k;l++) {
			int i1 = l * 20;
			int j1 = j - i1;
			if ( j1 < 0) break;
			int k1 = j1 > 20 ? i1 + 20 : i1 + j1;
			ApplicationInfo[] infos = fpb.subList(i1, k1).toArray(new ApplicationInfo[0]);
			fpf.get(l).mPa(infos);
		}
		notifyDataSetChanged();
	}

	public void mPc() {
		if (fpf != null) {
			int count = fpf.size();
			for (int i = 0; i < count; i++) {
				fpf.get(i).notifyDataSetChanged();
			}
		}
		
	}

}
