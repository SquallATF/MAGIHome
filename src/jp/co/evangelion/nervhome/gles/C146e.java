package jp.co.evangelion.nervhome.gles;

import javax.microedition.khronos.opengles.GL10;

//import jp.co.sharp.android.stereo3dlcd.Parallax;

import android.content.Context;
import android.opengl.GLES20;

public class C146e extends C139i {
	static final String TAG = C146e.class.getCanonicalName();
	protected static float Fb = 15F * FPo;
	public boolean fPc = true;
	protected float fd;
	protected int fe;
	protected int ff;
	protected float fg;
	protected float fh;
	protected float fi;
	protected int fj;
	
	public C146e(Context context, C138j p1) {
		super(context, p1);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (!fPc) {
			super.onDrawFrame(gl);
		} else {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            fPp.fPa.mPa();
            GLES20.glUniformMatrix4fv(mView, 1, false, fPp.fPa.fPd, 0);
            for(int j : fPp.fPe)
            {
                C144c c144c1 = fPp.fPd[j];
                if(c144c1.fc)
                {
                    GLES20.glViewport(0, 0, fj, ff);
                    float f1 = fi * (1.02F + -c144c1.fPd.y);
                    fPp.fPa.mPa(-fg - f1, fg - f1, -fh, fh, FPl, FPm);
                    GLES20.glUniformMatrix4fv(mProjection, 1, false, fPp.fPa.fPc, 0);
                    c144c1.mPa(mModel, mVertices, mTextureCoords);
                }
            }

            for(int n : fPp.fPe) 
            {
                C144c c144c = fPp.fPd[n];
                if(c144c.fc)
                {
                    GLES20.glViewport(fj, 0, fj, ff);
                    float f = fi * (1.02F + -c144c.fPd.y);
                    fPp.fPa.mPa(f + -fg, f + fg, -fh, fh, FPl, FPm);
                    GLES20.glUniformMatrix4fv(mProjection, 1, false, fPp.fPa.fPc, 0);
                    c144c.mPa(mModel, mVertices, mTextureCoords);
                }
            }

		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (!fPc) {
			super.onSurfaceChanged(gl, width, height);
		} else {
			fe = width;
			ff = height;
			// fd = (new Parallax()).get3dDepthSettingsFl();
			fd = 0.588f;
			fg = width * FPo;
			fh = height * FPo;
			fi = -0.35F * (Fb * fd);
			fj = width >> 1;
			fx.mPa(width, height);
		}
	}
}
