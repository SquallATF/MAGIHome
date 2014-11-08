package jp.co.evangelion.nervhome;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class DesktopIndicator extends ViewGroup implements AnimationListener {
	private View mIndicator;
	private boolean fpb = false;
	
	public DesktopIndicator(Context context) {
		super(context);
		initIndicator(context);
	}

	public DesktopIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initIndicator(context);
	}

	public DesktopIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initIndicator(context);
	}
	private void initIndicator(Context context){
		mIndicator=new SliderIndicator(context);
		addView(mIndicator);
	}
	
	public void indicate(float percent) {
		int offset=((int) (getWidth()*percent))-mIndicator.getLeft();
		((SliderIndicator)mIndicator).setOffset(offset);
		mIndicator.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int realHeight=SliderIndicator.INDICATOR_HEIGHT;
		mIndicator.measure(getWidth(), realHeight);
		int realHeightMeasurespec=MeasureSpec.makeMeasureSpec(realHeight, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, realHeightMeasurespec);

	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mIndicator.measure(getWidth(), 12);
		mIndicator.setLayoutParams(params);
		mIndicator.layout(0, 0, getWidth(), 12);
	}
	
	public void setType(int type){
		if(!fpb){
			FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(this.getLayoutParams());
			lp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
			setLayoutParams(lp);
			removeView(mIndicator);
			initIndicator(getContext());
			fpb = true;
		}
	}

	private class SliderIndicator extends View {
		public static final int INDICATOR_HEIGHT=12;
		private Drawable mSliderLine;
		private Drawable mSlider1;
		private Drawable mSlider2;
		private boolean fpe;
		
		public SliderIndicator(Context context) {
			super(context);
			Resources resources = context.getResources();
			Bitmap bitmap = ((BitmapDrawable)resources.getDrawable(R.drawable.slider_green)).getBitmap();
			mSlider1 = new FastBitmapDrawable(bitmap);
			mSlider2 = new FastBitmapDrawable(bitmap);
			mSliderLine = new FastBitmapDrawable(((BitmapDrawable)resources.getDrawable(R.drawable.slider_line)).getBitmap());
			mSliderLine.setBounds(0, 0, 695, 12);
		}
		
		public void setOffset(int i){
			int j = i - 6;
			if(595 < j){
				int k = j - 695;
				fpe = true;
				mSlider2.setBounds(k, 1, k + 113, 11);
			} else {
				fpe = false;
			}
			mSlider1.setBounds(j, 1, j + 113, 11);
		}
		
		@Override
		public void draw(Canvas canvas) {
			mSlider1.draw(canvas);
			if (fpe) {
				mSlider2.draw(canvas);
			}
			mSliderLine.draw(canvas);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}
}
