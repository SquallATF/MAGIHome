package jp.co.evangelion.nervhome.gles;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class C144c {
	protected C143b fa;
	protected C142f fb;
	protected boolean fc;
	public C141g fPd = new C141g();
	public C141g fPe = new C141g();
	protected float[] ff = new float[16];
	protected float[] fg = new float[16];
	protected float[] fh = new float[16];
	protected float[] fi = new float[32];
	protected float[] fj = new float[16];

	public C144c() {
		Matrix.setIdentityM(ff, 0);
	}
	
	public C142f mPa() {
		return fb;
	}
	
	public void mPa(float p) {
		fPe.x = p;
		fPe.y = 0;
		fPe.z = 0;
	}
	
	public void mPa(float p1, float p2, float p3) {
		fPd.x = p1;
		fPd.y = p2;
		fPd.z = p3;
	}
	
	public void mPa(int model, int vertices, int textureCoords){
		System.arraycopy(ff, 0, fh, 0, ff.length);
		final C141g fpd = fPd;
		boolean flag = fpd.x == 0 && fpd.y == 0 && fpd.z == 0;
		if(!flag) {
			Matrix.translateM(fh, 0, fPd.x, fPd.y, fPd.z);
		}
		if(fPe.x == 0) {
			System.arraycopy(ff, 0, fi, 0, ff.length);
		} else {
			Matrix.setRotateM(fi, 0, fPe.x, 1, 0, 0);
		}
		if(fPe.z !=0) {
			Matrix.setRotateM(fi, 16, fPe.z, 0, 0, 1);
			Matrix.multiplyMM(fi, 0, fi, 16, fi, 0);
		}
		Matrix.multiplyMM(fg, 0, fh, 0, fi, 0);
		GLES20.glUniformMatrix4fv(model, 1, false, fg, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fa.fPf);
		GLES20.glEnableVertexAttribArray(vertices);
		GLES20.glVertexAttribPointer(vertices, 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, fa.fPg);
		if(fb != null) {
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, fa.fPh);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fb.fPb);
            GLES20.glEnableVertexAttribArray(textureCoords);
            GLES20.glVertexAttribPointer(textureCoords, 2, GLES20.GL_FLOAT, false, 0, 0);
            GLES20.glDrawElements(fa.model, fa.count, GLES20.GL_UNSIGNED_SHORT, 0);
            GLES20.glDisableVertexAttribArray(textureCoords);			
		} else {
			GLES20.glDrawElements(fa.model, fa.count, GLES20.GL_UNSIGNED_SHORT, 0);
		}
		GLES20.glDisableVertexAttribArray(vertices);
	}
	
	public void mPa(C142f p) {
		fb = p;
	}
	
	public void mPa(C143b p) {
		fa = p;
	}
	
	public void mPa(boolean p) {
		fc = p;
	}
	
	public boolean mPb() {
		return fc;
	}
}
