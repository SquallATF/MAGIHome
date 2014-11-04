package jp.co.evangelion.nervhome.selector.gles;

import jp.co.evangelion.nervhome.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

public class C045c extends SurfaceView {
	static final String TAG = C045c.class.getSimpleName();
	Rect fb;
	private Bitmap fpc;
	private Paint fpd;

	public C045c(Context context) {
		super(context);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		fpd = new Paint();
		fpd.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		fpc = BitmapFactory.decodeResource(context.getResources(), R.drawable.lock);
		setZOrderOnTop(true);
	}

	private void mpa (int a){
		Canvas canvas = getHolder().lockCanvas();
		if (canvas == null) {
			Log.e(TAG, "draw canvas is null.");
			return;
		}
		fpd.setAlpha(a);
		draw(canvas);
		getHolder().unlockCanvasAndPost(canvas);
	}

	public void mPa() {
		mpa(0);
	}

	public void mPa(float p) {
		if (p > 20F) {
			mpa(0);
		} else {
			int i = 60 - 3 * (int)p;
			if (i % 5 == 0) {
				mpa(i);
			}
		}
	}

	public boolean mPa(float p1, float p2) {
		return p1 >= fb.left && p1 <= fb.right && p2 >= fb.top && p2 <= fb.bottom;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawBitmap(fpc, fb.left, fb.top, fpd);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    fb = new Rect(w / 2 - fpc.getWidth() / 2, h / 2 - fpc.getHeight() / 2, w / 2 + fpc.getWidth() / 2, h / 2 + fpc.getHeight() / 2);
	    fb.top += 24;
	    fb.bottom += 24;
	}
}
