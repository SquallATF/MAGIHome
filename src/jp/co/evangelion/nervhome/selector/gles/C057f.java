package jp.co.evangelion.nervhome.selector.gles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import jp.co.evangelion.nervhome.gles.C141g;
import jp.co.evangelion.nervhome.gles.C143b;

public class C057f {
	public static final String TAG = C057f.class.getSimpleName();

	public static int Mpa(C143b p) {
	    p.model = 4;
	    p.fPa = new float[] { -0.45262F, 0.90524F, 0.0F, 0.0F, 0.90524F, 0.45262F, 0.0F, 1.01209F, 0.0F, -0.58433F, 0.58433F, 0.58433F, 0.0F, 0.45262F, 0.90524F, -0.90524F, 0.45262F, 0.0F, -0.45262F, 0.0F, 0.90524F, 0.0F, 0.0F, 1.01209F, -0.90524F, 0.0F, 0.45262F, -1.01209F, 0.0F, 0.0F, 0.45262F, 0.90524F, 0.0F, 0.58433F, 0.58433F, 0.58433F, 0.90524F, 0.45262F, 0.0F, 0.90524F, 0.0F, 0.45262F, 1.01209F, 0.0F, 0.0F, 0.45262F, 0.0F, 0.90524F, 0.0F, 0.0F, 1.01209F, 0.0F, 0.90524F, -0.45262F, 0.58433F, 0.58433F, -0.58433F, 0.0F, 0.45262F, -0.90524F, 0.45262F, 0.0F, -0.90524F, 0.0F, 0.0F, -1.01209F, 0.90524F, 0.0F, -0.45262F, -0.58433F, 0.58433F, -0.58433F, -0.90524F, 0.0F, -0.45262F, -0.45262F, 0.0F, -0.90524F, 0.0F, -0.90524F, 0.45262F, -0.45262F, -0.90524F, 0.0F, 0.0F, -1.01209F, 0.0F, -0.58433F, -0.58433F, 0.58433F, -0.90524F, -0.45262F, 0.0F, 0.0F, -0.45262F, 0.90524F, 0.45262F, -0.90524F, 0.0F, 0.0F, -0.90524F, 0.45262F, 0.0F, -1.01209F, 0.0F, 0.58433F, -0.58433F, 0.58433F, 0.0F, -0.45262F, 0.90524F, 0.90524F, -0.45262F, 0.0F, 0.0F, -0.90524F, -0.45262F, 0.58433F, -0.58433F, -0.58433F, 0.0F, -0.45262F, -0.90524F, 0.0F, -0.90524F, -0.45262F, -0.58433F, -0.58433F, -0.58433F, 0.0F, -0.45262F, -0.90524F };
	    p.fPb = new short[] { 0, 1, 2, 3, 4, 1, 1, 0, 3, 3, 0, 5, 6, 7, 4, 4, 3, 6, 8, 6, 3, 3, 5, 8, 9, 8, 5, 1, 10, 2, 11, 12, 10, 10, 1, 11, 4, 11, 1, 13, 14, 12, 12, 11, 13, 15, 13, 11, 11, 4, 15, 16, 15, 4, 10, 17, 2, 18, 19, 17, 17, 10, 18, 12, 18, 10, 20, 21, 19, 19, 18, 20, 22, 20, 18, 18, 12, 22, 14, 22, 12, 17, 0, 2, 5, 0, 23, 0, 17, 23, 19, 23, 17, 24, 9, 5, 5, 23, 24, 25, 24, 23, 23, 19, 25, 21, 25, 19, 26, 27, 28, 29, 30, 27, 27, 26, 29, 31, 29, 26, 8, 9, 30, 30, 29, 8, 6, 8, 29, 29, 31, 6, 7, 6, 31, 32, 33, 34, 35, 36, 33, 33, 32, 35, 37, 35, 32, 15, 16, 36, 36, 35, 15, 13, 15, 35, 35, 37, 13, 14, 13, 37, 38, 32, 34, 39, 37, 32, 32, 38, 39, 40, 39, 38, 22, 14, 37, 37, 39, 22, 20, 22, 39, 39, 40, 20, 21, 20, 40, 27, 41, 28, 42, 43, 41, 41, 27, 42, 30, 42, 27, 25, 21, 40, 43, 42, 25, 24, 25, 42, 42, 30, 24, 9, 24, 30 };
	    p.fPc = new float[] { 0.362358F, 0.574905F, 0.425095F, 0.700379F, 0.425095F, 0.574905F, 0.318821F, 0.741469F, 0.425095F, 0.874526F, 0.275284F, 0.574905F, 0.212547F, 0.874526F, 0.14981F, 1.0F, 0.212547F, 0.700379F, 0.212547F, 0.574905F, 0.487832F, 0.574905F, 0.531368F, 0.741469F, 0.574905F, 0.574905F, 0.637642F, 0.700379F, 0.637642F, 0.574905F, 0.637642F, 0.874526F, 1.0F, 1.0F, 0.425095F, 0.449431F, 0.531368F, 0.408342F, 0.425095F, 0.275285F, 0.637642F, 0.275285F, 0.574905F, 0.14981F, 0.637642F, 0.449431F, 0.318821F, 0.408342F, 0.212547F, 0.449431F, 0.212547F, 0.275284F, 0.0F, 0.700379F, 0.062737F, 0.574905F, 0.0F, 0.574905F, 0.106274F, 0.741469F, 0.14981F, 0.574905F, 0.0F, 0.874526F, 0.787453F, 0.574905F, 0.85019F, 0.700379F, 0.85019F, 0.574905F, 0.743916F, 0.741469F, 0.85019F, 0.874526F, 0.700379F, 0.574905F, 0.85019F, 0.449431F, 0.743916F, 0.408342F, 0.85019F, 0.275285F, 0.0F, 0.449431F, 0.106274F, 0.408342F, 0.0F, 0.275285F };
	    return p.fPb.length;
	}


	public static C143b MPa() {
		C143b c143b = new C143b();
		c143b.count = Mpa(c143b);
		c143b.mPa();
		return c143b;
	}
	
	static List<C141g> Mb() {
		C143b c143b = new C143b();
		Mpa(c143b);
		ArrayList<C141g> list = new ArrayList<C141g>();
		HashMap<String, C141g> map = new HashMap<String, C141g>();
		for (int i = 0; i < c143b.fPa.length / 3; i++) {
			if (c143b.fPa[i * 3 + 2] != 0) {
				C141g c141g = new C141g(c143b.fPa[i * 3], c143b.fPa[i * 3 + 1],
						c143b.fPa[i * 3 + 2]);
				map.put(c141g.toString(), c141g);
			}
		}
		for (C141g item : map.values()) {
			list.add(item);
		}
		Collections.sort(list, new Comparator<C141g>() {

			@Override
			public int compare(C141g arg0, C141g arg1) {
				return (int) (-Math.signum(arg0.z - arg1.z));
			}
		});
		return list;
	}
}
