package jp.co.evangelion.nervhome.extended.drawer;

import jp.co.evangelion.nervhome.ApplicationInfo;
import jp.co.evangelion.nervhome.ApplicationsAdapter;
import jp.co.evangelion.nervhome.DragController;
import jp.co.evangelion.nervhome.DragSource;
import jp.co.evangelion.nervhome.Drawer;
import jp.co.evangelion.nervhome.Launcher;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;

public class MagiDrawer extends LinearLayout implements ViewPager.OnPageChangeListener, OnItemClickListener,
		OnItemLongClickListener, DragSource, Drawer {
	
	private DragController mDragger;
	//mViewGroug
	private ViewGroup fpb;
	//mLauncher
	private Launcher fpc;
	private boolean fpd;
	//mAdapter
	private ApplicationsAdapter fpe;
	//mDataSetObserver
	private AdapterDataSetObserver fpf;
	//mViewPager
	private final ViewPager fpg;
	//mViewPagerAdapter
	private final ViewPagerAdapter fph;
	//mDrawerIndicator
	private final DrawerIndicator fpi;

	public MagiDrawer(Context context) {
		this(context, null);
	}

	public MagiDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		setBackgroundColor(Color.BLACK);
		fpi = new DrawerIndicator(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 26);
		lp.topMargin = 6;
		fpi.setLayoutParams(lp);
		addView(fpi);
		fpg = new ViewPager(context);
		fpg.setOffscreenPageLimit(5);
		fpg.setOnPageChangeListener(this);
		fph = new ViewPagerAdapter(context, this, this);
		fpg.setAdapter(fph);
		addView(fpg);
	}

	@Override
	public void setDragger(DragController dragger) {
		mDragger = dragger;
		fpb = (ViewGroup)mDragger;
	}

	@Override
	public void setLauncher(Launcher launcher) {
		fpc = launcher;

	}

	@Override
	public void setAdapter(ApplicationsAdapter adapter) {
		if (fpe != null) {
			fpe.unregisterDataSetObserver(fpf);
		}
		fpe = adapter;
		if (fpe != null) {
			fpf = new AdapterDataSetObserver();
			fpe.registerDataSetObserver(fpf);
			fph.mPa(ApplicationsAdapter.allItems);
			fpi.mPb(ApplicationsAdapter.allItems.size() / 20 + 1);
		} else {
			fpi.mPb(0);
		}

	}

	@Override
	public void onDropCompleted(View target, boolean success) {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if(!view.isInTouchMode()) {
			return false;
		}
		ApplicationInfo info = new ApplicationInfo((ApplicationInfo)parent.getItemAtPosition(position));
		mDragger.startDrag(view, this, info, 1);
		fpc.closeAllApplications();
		return true;

	}

	@Override
	public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
		ApplicationInfo info = (ApplicationInfo)parent.getAdapter().getItem(position);
		fpc.startActivitySafely(info.intent);
	}

	@Override
	public void open() {
		if (getParent() == null) {
			fpb.addView(this);
		}
		fpc.mPk();
		setVisibility(View.VISIBLE);
		fph.mPc();
	}

	@Override
	public void close() {
		fpb.removeView(this);		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		fpi.mPa(position);
		
	}
	
	@Override
	protected void onAnimationEnd() {
		post(new Runnable(){

			@Override
			public void run() {
				if(fpd) {
					setVisibility(View.VISIBLE);
				} else {
					fpb.removeView(MagiDrawer.this);
				}
				
			}
			
		});
	}

	private class AdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			fph.mPa(ApplicationsAdapter.allItems);
			int count = fph.getCount();
			fpi.mPb(count);
			if (getParent() != null) {
				int j = fpg.getCurrentItem();
				if (count <= j && j > 0)
					fpg.setCurrentItem(j - 1);
				invalidate();
			}
		}
	}
}
