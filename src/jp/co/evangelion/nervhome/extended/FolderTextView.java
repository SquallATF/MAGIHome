package jp.co.evangelion.nervhome.extended;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class FolderTextView extends TextView {
	
	public FolderTextView(Context context) {
		super(context);
	}

	public FolderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FolderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void draw(Canvas canvas) {
		getCompoundDrawables()[1].setBounds(0, 0, 96, 96);
		super.draw(canvas);
	}
}
