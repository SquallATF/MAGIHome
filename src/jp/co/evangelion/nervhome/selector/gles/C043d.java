package jp.co.evangelion.nervhome.selector.gles;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.SystemClock;
import jp.co.evangelion.nervhome.R;
import jp.co.evangelion.nervhome.gles.C140h;
import jp.co.evangelion.nervhome.gles.C141g;
import jp.co.evangelion.nervhome.gles.C142f;
import jp.co.evangelion.nervhome.gles.C143b;
import jp.co.evangelion.nervhome.gles.C144c;
import jp.co.evangelion.nervhome.gles.C145d;
import jp.co.evangelion.nervhome.gles.C147a;

public class C043d extends C140h {
	static final String TAG = C043d.class.getSimpleName();
	public boolean fPg;
	private int fph;
	private long fpi;

	private static float Mpa(int p) {
		return (360F + 360F / 7 * -p) % 360F;
	}

	public static C043d MPa(Resources resources) {
		C043d c043d = new C043d();
		c043d.fPb = new C143b[4];
		c043d.fPc = new C142f[37];
		c043d.fPd = new C144c[38];
		C143b c143b = C145d.MPa(2.6F, 2.6F);
		c043d.fPb[0] = c143b;
		C142f c142f = C142f.MPa(BitmapFactory.decodeResource(resources,
				R.drawable.soner_up));
		c043d.fPc[1] = c142f;
		C144c c144c = new C144c();
		c144c.mPa(c143b);
		c144c.mPa(c142f);
		c144c.mPa(0, 0, 1.5F);
		c144c.mPa(270F);
		c144c.mPa(true);
		c043d.fPd[0] = c144c;
		C142f c142f1 = C142f.MPa(BitmapFactory.decodeResource(resources,
				R.drawable.soner_down));
		c043d.fPc[1] = c142f1;
		C144c c144c1 = new C144c();
		c144c1.mPa(c143b);
		c144c1.mPa(c142f1);
		c144c1.mPa(0, 0, -1.6F);
		c144c1.mPa(90F);
		c144c1.mPa(true);
		c043d.fPd[1] = c144c1;
		C143b c143b1 = C060i.MPa();
		c043d.fPb[2] = c143b1;
		C142f c142f2 = C142f.MPa(BitmapFactory.decodeResource(resources,
				R.drawable.soner));
		c043d.fPc[0] = c142f2;
		C144c c144c2 = new C144c();
		c144c2.mPa(c143b1);
		c144c2.mPa(c142f2);
		c144c2.mPa(0, 0, 1.5F);
		c144c2.mPa(270F);
		c144c2.mPa(true);
		c043d.fPd[2] = c144c2;
		C144c c144c3 = new C144c();
		c144c3.mPa(c143b1);
		c144c3.mPa(c142f2);
		c144c3.mPa(0, 0, -1.6F);
		c144c3.mPa(90F);
		c144c3.mPa(true);
		c043d.fPd[3] = c144c3;
		C143b c143b2 = C057f.MPa();
		c043d.fPb[1] = c143b2;
		// C142f c142f3 = C142f.bindPVRTC(resources, R.raw.polytope);
		C142f c142f3 = C142f.MPa(BitmapFactory.decodeResource(resources,
				R.drawable.polytope));
		c043d.fPc[3] = c142f3;
		C144c c144c4 = new C144c();
		c144c4.mPa(c143b2);
		c144c4.mPa(c142f3);
		c144c4.mPa(true);
		c043d.fPd[4] = c144c4;
		C143b c143b3 = C047b.MPa();
		c043d.fPb[1] = c143b3;
		List<C141g> list = C057f.Mb();
		for (int j = 0; j < list.size(); j++) {
			C141g c141g = list.get(j);
			C142f c142f4 = C142f.MPa(BitmapFactory.decodeResource(resources,
					R.drawable.joint_a1 + j));
			c043d.fPc[j + 4] = c142f4;
			C144c c144c5 = new C144c();
			c144c5.mPa(c141g.x, c141g.y, c141g.z);
			c144c5.mPa(c143b3);
			c144c5.mPa(c142f4);
			c144c5.mPa(true);
			c043d.fPd[j + 5] = c144c5;
		}
		c043d.fph = list.size();
		c043d.fPe = new int[c043d.fPd.length];
		for (int i = 0; i < c043d.fPe.length; i++) {
			c043d.fPe[i] = i;
		}
		return c043d;
	}

	private void mpa(float p1, float p2, float p3) {
		final float f1 = (float) (Math.PI / 180) * p1;
		C147a c147a = fPa;
		float f2 = (float) (p2 * Math.cos(f1));
		float f3 = (float) (-p2 * Math.sin(f1));
		c147a.fPa.x = f2;
		c147a.fPa.y = f3;
		c147a.fPa.z = p3;
	}

	private void mpb(int p) {
		for (int j = 0; j < 7; j++) {
			int k = p - j;
			if (Math.abs(k) > 3)
				k = (int) ((7 % k) * (-1F * Math.signum(k)));
			if (k < 0)
				k = 3 + k * -1;
			fPe[31 + (6 - k)] = j + 31;
		}
	}

	public float mPa(int p1, float p2, Type type) {
		float f1 = (360F + (Mpa(p1) + p2)) % 360F;
		float f2 = f1 % (360F / 7);
		float f3 = Math.min(f2, 360F / 7 - f2);
		float f5 = (type == Type.HOME ? 0.68F : 1.78F) + 1.02F;
		if (type == Type.HOME && f3 <= 20F)
			f5 -= 0.01F * (20F - f3);
		float f6 = type == Type.HOME ? 0 : 0.15F;
		mpa(f1, f5, f6);
		float f7 = (360F + -(f1 - 90F)) % 360F;
		for (int i = 0; i < fph; i++) {
			fPd[i + 5].fPe.z = f7;
		}
		if (fPd[31] != null) {
			int k = -1;
			float f8 = Float.MAX_VALUE;
			float f12;
			for (int l = 0; l < 7; l++, f8 = f12) {
				C144c c144c1 = fPd[l + 31];
				c144c1.fPe.z = f7;
				float f11 = (float) (Math.PI / 180)
						* ((360F + 360F / 7 * -l) % 360F);
				c144c1.fPd.x = (float) (1.0199999809265137 * Math.cos(f11));
				c144c1.fPd.y = (float) (-1.0199999809265137 * Math.sin(f11));
				if (type == Type.PREVIEW) {
					f12 = (float) Math.hypot(fPa.fPa.x - c144c1.fPd.x,
							fPa.fPa.y - c144c1.fPd.y);
					if (f12 < f8) {
						k = l;
						continue;
					}
				}
				f12 = f8;
			}
			if (type == Type.PREVIEW && f3 <= 20F) {
				float f9 = (float) (Math.PI / 180)
						* ((360F + 360F / 7 * -k) % 360F);
				float f10 = 1.02F + 0.015F * (20F - f3);
				C144c c144c = fPd[k + 31];
				c144c.fPd.x = (float) (f10 * Math.cos(f9));
				c144c.fPd.y = (float) (-f10 * Math.sin(f9));
			}
		}
		if (p2 == 0)
			mpb(p1);
		return f3;
	}

	public void mPa(int p1, int p2) {
		C143b c143b = C059h.MPa(p1, p2);
		fPb[2] = c143b;
		for (int i = 0; i < 7; i++) {
			C144c c144c = new C144c();
			c144c.mPa(c143b);
			c144c.mPa(false);
			fPd[i + 31] = c144c;
		}
	}

	public void mPa(int p1, int p2, float p3, Type type, C032a c032a) {
		final long sleep = 41;
		float f1 = 4F;
		int k = p1 - p2;
		if (Math.abs(k) > 3)
			k = (int) ((float) (7 % k) * (-1F * Math.signum(k)));
		if (Math.abs(k) > 1)
			f1 = 3F * (f1 * (float) Math.abs(k));
		float f2 = 360F / 7 * (float) k - p3;
		float f3 = Math.signum(f2);
		for (;f2 != 0F;) {
			fpi = SystemClock.currentThreadTimeMillis();
			float f4 = fPg ? 2F : f1;
			float f5 = f4 * f3;
			if (Math.abs(f2) < Math.abs(f5)) {
				mpb(p2);
				f5 = f2;
			}
			p3 += f5;
			float f6 = mPa(p1, p3, type);
			f2 -= f5;
			if (!c032a.mPa(f6))
				break;
			long l = SystemClock.currentThreadTimeMillis() - fpi;
			if (l < sleep)
				SystemClock.sleep(sleep - l);

		}
		c032a.mPa();
	}

	public final void mPa(int p1, Bitmap bitmap) {
		C144c c144c = fPd[p1 + 31];
		C142f c142f = c144c.mPa();
		if (c142f != null) {
			int texture[] = new int[1];
			texture[0] = c142f.fPb;
			GLES20.glDeleteTextures(1, texture, 0);
		}
		C142f c142f1 = C142f.MPa(bitmap);
		super.fPc[p1 + 30] = c142f1;
		c144c.mPa(c142f1);
		c144c.mPa(true);
	}

	public final void mPa(int p1, C032a c032a) {
		final long sleep = 41;
		mPa(p1, 0.0F, Type.HOME);
		fpi = SystemClock.currentThreadTimeMillis();
		for (float f = 0.68F; f < 1.78F; f += 0.2F) {
			mpa(Mpa(p1), 1.02F + f, 0.15F);
			c032a.mPa(f);
			long l = SystemClock.currentThreadTimeMillis() - fpi;
			if (l < sleep)
				SystemClock.sleep(sleep - l);
		}

		mPa(p1, 0.0F, Type.PREVIEW);
		mpb(p1);
		c032a.mPa();
	}

	public final void mPb(int p1, C032a c032a) {
		final long sleep = 41;
		mPa(p1, 0.0F, Type.PREVIEW);
		fpi = SystemClock.currentThreadTimeMillis();
		for (float f = 1.78F; f > 0.88F; f -= 0.2F) {
			mpa(Mpa(p1), 1.02F + f, 0.0F);
			c032a.mPa(f);
			long l = SystemClock.currentThreadTimeMillis() - fpi;
			if (l < sleep)
				SystemClock.sleep(sleep - l);
		}

		mPa(p1, 0.0F, Type.HOME);
		mpb(p1);
		c032a.mPa();
	}

}
