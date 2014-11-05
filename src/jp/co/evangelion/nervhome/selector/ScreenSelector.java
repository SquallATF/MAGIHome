package jp.co.evangelion.nervhome.selector;

import jp.co.evangelion.nervhome.gles.C138j;
import jp.co.evangelion.nervhome.gles.C139i;
import jp.co.evangelion.nervhome.gles.C142f;
import jp.co.evangelion.nervhome.gles.C144c;
import jp.co.evangelion.nervhome.selector.gles.C032a;
import jp.co.evangelion.nervhome.selector.gles.C043d;
import jp.co.evangelion.nervhome.selector.gles.C045c;
import jp.co.evangelion.nervhome.selector.gles.Type;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.os.Process;
import android.os.SystemClock;

public class ScreenSelector extends GLSurfaceView {

	static final String TAG = ScreenSelector.class.getSimpleName();
	public Type fPb;
	protected C139i fc;
	protected C148b fd;
	protected C150a fe;
	public float fPf;
	public float fPg;
	public int fPh;
	protected C045c fi;
	public AutoRotateTask fPj;
	private long fpk;
	private boolean fpl;
	private boolean fpm;

	public ScreenSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		setRenderer(createRenderer(new C138j() {
			@Override
			public void mPa() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
				fc.fPp = C043d.MPa(getResources());

			}

			@Override
			public void mPa(int p1, int p2) {
				((C043d) fc.fPp).mPa(p1, p2);
				fd.mPz();
				if (fi == null) {
					post(new Runnable() {

						@Override
						public void run() {
							fi = new C045c(getContext());
							((ViewGroup) getParent()).addView(fi);
						}

					});
				}
				fpl = true;
			}

		}));
		setRenderMode(RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		fPb = Type.HOME;
	}

	int md(int i) {
		return (i + 7) % 7;
	}

	public void mPa(int screen, float f) {
		final long sleep = 41;
		if (!fpl)
			return;
		if (fPg == 0) {
			fPg = -f;
			if (fPb == Type.HOME)
				mpa(screen, true);
			fpk = SystemClock.currentThreadTimeMillis();
		} else {
			fPg = -f + fPg;
		}
		float f2 = ((C043d) fc.fPp).mPa(screen, 0.07f * fPg, fPb);
		long l = SystemClock.currentThreadTimeMillis() - fpk;
		if (l < sleep) {
			SystemClock.sleep(sleep - l);
		}
		requestRender();
		fpk = SystemClock.currentThreadTimeMillis();
		if (fPb == Type.PREVIEW) {
			fi.mPa(f2);
		}
	}

	public void mPa(final int i, View view) {
		final Bitmap bitmap = C142f.MPa(view);
		queueEvent(new Runnable() {

			@Override
			public void run() {
				C043d c043d = (C043d) fc.fPp;
				if (c043d != null) {
					c043d.mPa(i, bitmap);
				}
			}

		});
	}

	public void mPa(final int i, final int j) {
		if (!fpl) {
			postDelayed(new Runnable() {
				@Override
				public void run() {
					mPa(i, j);
				}
			}, 500);
		} else {
			((C043d) fc.fPp).mPa(i, j, 0.07f * fPg, fPb, new C032a() {

				@Override
				public void mPa() {
					fPg = 0;
					if (fPb == Type.HOME && fd != null) {
						C148b c148b = fd;
						c148b.mPe(md(j));
					}

				}

				@Override
				public boolean mPa(float p) {
					if (fPb == Type.PREVIEW)
						fi.mPa(p);
					requestRender();
					return true;
				}

			});
		}
	}

	public void mPa(C148b c148b) {
		fd = c148b;
	}

	public void mPa(C150a c150a) {
		fe = c150a;
	}

	public boolean mPa() {
		return fpl;
	}

	public void mPa(int i) {
		if (fPb == Type.PREVIEW) {
			i = fPh;
		}
		mpa(i, fPb == Type.PREVIEW);
		((C043d) fc.fPp).mPa(i, 0, fPb);
		requestRender();
	}

	public void mPb(int screen) {
		if (fPb != Type.PREVIEW) {
			fpm = false;
			mpa(screen, true);
			fPh = screen;
			((C043d) fc.fPp).mPa(screen, new C032a() {

				@Override
				public void mPa() {
					fi.mPa(0);
					autoRotate(fPh);
					fPb = Type.PREVIEW;

				}

				@Override
				public boolean mPa(float paramFloat) {
					requestRender();
					return true;
				}

			});
		}

	}

	public void mPc(final int screen) {
		if (fPb != Type.HOME) {
			mpc();
			fPb = Type.HOME;
			fi.mPa();
			((C043d) fc.fPp).mPb(screen, new C032a() {

				@Override
				public void mPa() {
					mPa(screen);
				}

				@Override
				public boolean mPa(float paramFloat) {
					requestRender();
					return true;
				}

			});
		}
	}

	protected C139i createRenderer(C138j c138j) {
		fc = new C139i(getContext(), c138j);
		return fc;
	}

	private void mpa(int i, boolean flag) {
		C144c c144c = fc.fPp.fPd[i + 31];
		if (c144c.mPb() != flag)
			c144c.mPa(flag);
		requestRender();
	}

	private void mpb() {
		if (mpd()) {
			((C043d) fc.fPp).fPg = false;
		}
	}

	private void mpc() {
		if (mpd()) {
			mpb();
			fPj.cancel(true);
		}
	}

	private boolean mpd() {
		return ((C043d) fc.fPp).fPg;
	}

	void autoRotate(int i) {
		if (fPj != null) {
			Log.e(TAG, "autoRotate - in progress");
			return;
		} else {
			((C043d) fc.fPp).fPg = true;
			fPj = new AutoRotateTask();
			fPj.execute(i);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (fPb == Type.PREVIEW) {
			mpc();
			fPb = Type.HOME;
			fi.mPa();
			requestRender();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() > 1 || !fpl || fPb != Type.PREVIEW)
			return true;
		float f = event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			fPf = f;
			fPg = 0;
			fpm = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (fpm && !mpd()) {
				mPa(fPh, -(f - fPf));
			}
			break;
		case MotionEvent.ACTION_UP:
			if (fpm) {
				if (mpd()) {
					mpb();
				} else {
					if (fPb == Type.PREVIEW && Math.abs(fPg) <= 4
							&& fi.mPa(f, event.getY())) {
						fe.mPc(fPh);
					} else {
						int i = fPh;
						int j = fPh;
						int k = Math.abs(fPg) > 200F ? (int) Math.signum(-fPg)
								: 0;
						int l = (7 + (k + j)) % 7;
						mPa(i, l);
						fPh = l;
					}
				}
				fpm = false;
			}
			break;
		}
		fPf = f;
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		super.surfaceChanged(holder, format, w, h);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		fpl = false;
		super.surfaceDestroyed(holder);
	}

	private class AutoRotateTask extends AsyncTask<Integer, Float, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {
			int j;
			int i = arg0[0];
			while (mpd()) {
				j = md(i - 1);
				((C043d) fc.fPp).mPa(i, j, 0, Type.PREVIEW, new C032a() {

					@Override
					public void mPa() {
						SystemClock.sleep(10L);

					}

					@Override
					public boolean mPa(float p) {
						if (fPb == Type.PREVIEW) {
							publishProgress(p);
						}
						return fPb == Type.PREVIEW;
					}

				});
				i = j;
			}
			return i;
		}

		@Override
		protected void onCancelled() {
			fPj = null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			fPh = result;
			onCancelled();
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			if (fPb == Type.PREVIEW) {
				fi.mPa(values[0]);
				requestRender();
			}
		}
	}
}
