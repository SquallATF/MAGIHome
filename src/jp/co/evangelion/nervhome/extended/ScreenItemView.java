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

	  private static final int Fpa = Color.rgb(255, 173, 0);
	  private Paint fpb = new Paint();
	  private String fpc;
	  private int fpd;
	  private Drawable fpe;
	  private Rect fpf;
	
	public ScreenItemView(Context context) {
		this(context, null);
    }

    public ScreenItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScreenItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    	super(context, attrs, defStyleAttr);
        fpb.setAntiAlias(true);
        fpb.setColor(Fpa);
        fpb.setTextSize(13F);
    }
    
    public void draw(Canvas canvas) {
    	super.draw(canvas);
    	canvas.drawText(fpc, 0, fpd, 26.0F, 33.0F, fpb);
    	fpe.setBounds(fpf);
    	fpe.draw(canvas);
    }
    
    //setTypeface
    public final void mPa(Typeface typeface) {
    	fpb.setTypeface(typeface);
    }
    
    public final void mPa(Drawable drawable) {
    	if (drawable != null) {
    		fpe = drawable;
    		fpf = new Rect(42, 45, 138, 141);
    	}
    }
    
    public final void mPa(CharSequence charSequence) {
    	if (charSequence != null) {
            fpc = charSequence.toString();
            fpd = fpb.breakText(fpc, true, 128.0F, null);    		
    	}
    }

}
