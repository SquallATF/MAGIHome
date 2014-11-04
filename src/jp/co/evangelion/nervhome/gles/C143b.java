package jp.co.evangelion.nervhome.gles;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class C143b {
	public float[] fPa;
	public short[] fPb;
	public float[] fPc;
	public int model;
	public int count;
	public int fPf;
	public int fPg;
	public int fPh;
	
	public void mPa() {
		int[] buff = new int[3];
		GLES20.glGenBuffers(buff.length, buff, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buff[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 4 * fPa.length, FloatBuffer.wrap(fPa), GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buff[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 2 * fPb.length, ShortBuffer.wrap(fPb), GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buff[2]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 4 * fPc.length, FloatBuffer.wrap(fPc), GLES20.GL_STATIC_DRAW);
		fPf = buff[0];
		fPg = buff[1];
		fPh = buff[2];
		fPa = null;
		fPb = null;
		fPc = null;
	}

}
