package jp.co.evangelion.nervhome.extended.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DrawerCell extends TextView {
	
	public DrawerCell(Context context) {
		this(context, null);
	}

	public DrawerCell(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawerCell(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void draw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables[1] != null) {
			drawables[1].setBounds(0, 16, 96, 112);
		}
		super.draw(canvas);
	}
}
