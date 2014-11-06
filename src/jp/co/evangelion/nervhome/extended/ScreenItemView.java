package jp.co.evangelion.nervhome.extended;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ScreenItemView extends View {

	private static final int mColor = Color.rgb(255, 173, 0);
	private Paint mPaint = new Paint();
	private String mTitle;
	private int mEnd;
	private Drawable mIcon;
	private Rect mBounds;

	public ScreenItemView(Context context) {
		this(context, null);
	}

	public ScreenItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScreenItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint.setAntiAlias(true);
		mPaint.setColor(mColor);
		mPaint.setTextSize(13f);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawText(mTitle, 0, mEnd, 26f, 33f, mPaint);
		mIcon.setBounds(mBounds);
		mIcon.draw(canvas);
	}

	public void setTypeface(Typeface typeface) {
		mPaint.setTypeface(typeface);
	}

	public void setIcon(Drawable icon) {
		if (icon != null) {
			mIcon = icon;
			mBounds = new Rect(42, 45, 138, 141);
		}
	}

	public void setText(CharSequence charSequence) {
		if (charSequence != null) {
			mTitle = charSequence.toString();
			mEnd = mPaint.breakText(mTitle, true, 128f, null);
		}
	}

}
