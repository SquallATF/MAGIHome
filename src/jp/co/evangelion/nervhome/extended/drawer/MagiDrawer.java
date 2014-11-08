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
	private ViewGroup mViewGroug;
	private Launcher mLauncher;
	private boolean fpd;
	private ApplicationsAdapter mAdapter;
	private AdapterDataSetObserver mDataSetObserver;
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private DrawerIndicator mDrawerIndicator;

	public MagiDrawer(Context context) {
		this(context, null);
	}

	public MagiDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		setBackgroundColor(Color.BLACK);
		mDrawerIndicator = new DrawerIndicator(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 26);
		lp.topMargin = 6;
		mDrawerIndicator.setLayoutParams(lp);
		addView(mDrawerIndicator);
		mViewPager = new ViewPager(context);
		mViewPager.setOffscreenPageLimit(5);
		mViewPager.setOnPageChangeListener(this);
		mViewPagerAdapter = new ViewPagerAdapter(context, this, this);
		mViewPager.setAdapter(mViewPagerAdapter);
		addView(mViewPager);
	}

	@Override
	public void setDragger(DragController dragger) {
		mDragger = dragger;
		mViewGroug = (ViewGroup)mDragger;
	}

	@Override
	public void setLauncher(Launcher launcher) {
		mLauncher = launcher;

	}

	@Override
	public void setAdapter(ApplicationsAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		if (mAdapter != null) {
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);
			mViewPagerAdapter.mPa(ApplicationsAdapter.allItems);
			mDrawerIndicator.mPb(ApplicationsAdapter.allItems.size() / 20 + 1);
		} else {
			mDrawerIndicator.mPb(0);
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
		mLauncher.closeAllApplications();
		return true;

	}

	@Override
	public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
		ApplicationInfo info = (ApplicationInfo)parent.getAdapter().getItem(position);
		mLauncher.startActivitySafely(info.intent);
	}

	@Override
	public void open() {
		if (getParent() == null) {
			mViewGroug.addView(this);
		}
		mLauncher.mPk();
		setVisibility(View.VISIBLE);
		mViewPagerAdapter.mPc();
	}

	@Override
	public void close() {
		mViewGroug.removeView(this);		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		mDrawerIndicator.mPa(position);
		
	}
	
	@Override
	protected void onAnimationEnd() {
		post(new Runnable(){

			@Override
			public void run() {
				if(fpd) {
					setVisibility(View.VISIBLE);
				} else {
					mViewGroug.removeView(MagiDrawer.this);
				}
				
			}
			
		});
	}

	private class AdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			mViewPagerAdapter.mPa(ApplicationsAdapter.allItems);
			int count = mViewPagerAdapter.getCount();
			mDrawerIndicator.mPb(count);
			if (getParent() != null) {
				int j = mViewPager.getCurrentItem();
				if (count <= j && j > 0)
					mViewPager.setCurrentItem(j - 1);
				invalidate();
			}
		}
	}
}
