package jp.co.evangelion.nervhome.gles;

import android.opengl.Matrix;

public class C147a {
	public C141g fPa = new C141g();
	public C141g fPb = new C141g();
	public float[] fPc = new float[16];
	public float[] fPd = new float[16];
	
	public void mPa(){
		Matrix.setLookAtM(this.fPd, 0, fPa.x, fPa.y, fPa.z, fPb.x, fPb.y, fPb.z, 0.0F, 0.0F, 1.0F);
	}
	
	public void mPa(float left, float right, float bottom, float top, float near, float far) {
		Matrix.frustumM(fPc, 0, left, right, bottom, top, near, far);
	}
}
