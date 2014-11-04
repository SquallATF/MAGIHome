package jp.co.evangelion.nervhome.gles;

import android.annotation.SuppressLint;

public class C141g {
	public float x;
	public float y;
	public float z;

	public C141g(){
		
	}

	public C141g(float p1, float p2, float p3) {
		x = p1;
		y = p2;
		z = p3;
	}
	
	@Override
	public boolean equals(Object o) {
		C141g obj = (C141g) o;
		return x == obj.x && y == obj.y && z == obj.z;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("%f %f %f", x, y, z);
	}
}
