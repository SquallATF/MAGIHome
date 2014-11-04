package jp.co.evangelion.nervhome.extended.drawer;

import jp.co.evangelion.nervhome.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class DrawerIndicator extends View {
	
	private final Drawable mPagerDotNormal;
	private final Drawable mPagerDotSelected;
	private int fpc;
	private int fpd;
	private int fpe;

	public DrawerIndicator(Context context) {
		this(context, null);
	}

	public DrawerIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		final Resources resources = context.getResources();
		mPagerDotNormal = resources.getDrawable(R.drawable.pager_dot_normal);
		mPagerDotSelected = resources.getDrawable(R.drawable.pager_dot_selected);
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		int left = fpd;
		for (int i=0;i<fpc ; i++) {
			final Drawable drawable = i == fpe ?
					mPagerDotSelected : mPagerDotNormal;
			drawable.setBounds(left, 0, left + 26, 26);
			left += 41;			
		}
	}

	public void mPa(int position) {
		fpe = position;
		invalidate();
		
	}

	public void mPb(int i) {
		fpc = i;
		fpd = 360 - (15 + (26 * fpc) / 2);
		if (fpd < 0) {
			fpd = 0;
		}
		
	}

}
