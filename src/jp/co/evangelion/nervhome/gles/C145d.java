package jp.co.evangelion.nervhome.gles;

public class C145d {
	public static C143b MPa(float p1, float p2) {
		C143b c143b = new C143b();
		c143b.model = 5;
		c143b.fPa = new float[] { -p1 / 2, 0, -p2 / 2, p1 / 2, 0, -p2 / 2, p1 / 2, 0, p2 / 2, -p1 / 2, 0, p2 / 2 };
		c143b.fPb = new short[] { 0, 1, 3, 2 };
		c143b.fPc = new float[] { 0, 1, 1, 1, 1, 0, 0, 0 };
		c143b.count = c143b.fPb.length;
		c143b.mPa();
		return c143b;
	}
}
