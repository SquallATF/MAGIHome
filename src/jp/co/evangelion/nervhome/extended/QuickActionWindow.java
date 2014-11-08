package jp.co.evangelion.nervhome.extended;

import jp.co.evangelion.nervhome.ActionButton;
import jp.co.evangelion.nervhome.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class QuickActionWindow extends PopupWindow implements OnTouchListener {
	
	private Context mContext;
	private OnClickListener mOnClickListener;
	private View mAnchor;
	
	public QuickActionWindow(Context context) {
		super(context);
		mContext = context;
	}
	
	public void mPa(OnClickListener onClickListener){
		mOnClickListener = onClickListener;
	}
	
	public void mPa(View view){
		mAnchor = view;
	    setTouchable(true);
	    setOutsideTouchable(true);
	    setBackgroundDrawable(new ColorDrawable(0));
		int width = mAnchor.getWidth();
		int height = mAnchor.getHeight();
		if( mAnchor instanceof ActionButton){
			height += 10;
		}
		setWidth(width);
		setHeight(height);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
		ContentView contentView = new ContentView(mContext, mAnchor);
		contentView.setLayoutParams(lp);
		contentView.setOnTouchListener(this);
		setContentView(contentView);
		showAsDropDown(mAnchor, 0, -1 * height);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if(mOnClickListener != null && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			mOnClickListener.onClick(mAnchor);
			return true;
		}
		return false;
	}

	class ContentView extends View {
		private Drawable mIcon;
		
		private ContentView(Context context, View view) {
			super(context, null, 0);
			mIcon = context.getResources().getDrawable(R.drawable.edit_icon);
			int width = mIcon.getIntrinsicWidth();
			int left;
			int top;
			int right;
			int bottom;
			if(view instanceof ScreenItemView){
				left = 113;
				top = 36;
			} else {
				left = 96;
				top = 0;
			}
			right = top + width;
			bottom = width + top;
			mIcon.setBounds(left, top, right, bottom);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			mIcon.draw(canvas);
		}
	}
}
