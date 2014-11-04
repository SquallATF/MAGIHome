package jp.co.evangelion.nervhome.selector.gles;

import jp.co.evangelion.nervhome.gles.C143b;
import jp.co.evangelion.nervhome.gles.C145d;

public class C059h {
	public static final String TAG = C059h.class.getSimpleName();

	public static C143b MPa(float p1, float p2) {
		return C145d.MPa(0.82F * (p1 / p2), 0.82F);
	}
}
