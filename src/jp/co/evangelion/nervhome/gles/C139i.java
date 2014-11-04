package jp.co.evangelion.nervhome.gles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.co.evangelion.nervhome.R;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class C139i implements Renderer {

	public static float FPl = 0.1F;
	public static float FPm = 10.0F;
	public static float FPn = 720.0F;
	public static float FPo = 0.5F * (FPl / FPn);
	static final String TAG = C139i.class.getSimpleName();
	public C140h fPp;
	protected Context mContext;
	protected int mProgram;
	protected int mProjection;
	protected int mView;
	protected int mModel;
	protected int mVertices;
	protected int mTextureCoords;
	protected C138j fx;
	
	public C139i(Context context, C138j p1) {
		mContext = context;
		fx = p1;
	}
	
	private static void checkGlError(String op) {
		int error = GLES20.glGetError();
		if (error != 0) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
	
	private static String loadAsset(Resources resources, int id) {
		StringBuffer stringBuilder = new StringBuffer();
		try {
			final InputStreamReader reader = new InputStreamReader(
					resources.openRawResource(id));
			final BufferedReader bufferReader = new BufferedReader(reader);
			for (;;) {
				String str = bufferReader.readLine();
				if (str == null) {
					break;
				}
				stringBuilder.append(str);
				stringBuilder.append(System.getProperty("line.separator"));
			}
		} catch (IOException localIOException) {
			Log.e(TAG, "Failed to load asset file.", localIOException);
		}
		return stringBuilder.toString();
	}
	
	private static int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int compiled[] = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e(TAG, "Could not compile shader " + shaderType + ":");
				Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				return 0;
			}
		}
		return shader;
	}

	
	@Override
	public void onDrawFrame(GL10 gl) {
		int i = 0;
		fPp.fPa.mPa();
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		checkGlError("glUseProgram");
		GLES20.glUniformMatrix4fv(mProjection, 1, false, fPp.fPa.fPc, 0);
		GLES20.glUniformMatrix4fv(mView, 1, false, fPp.fPa.fPd, 0);
		int ai[] = fPp.fPe;
		for (int j = ai.length; i < j; i++) {
			int k = ai[i];
			C144c c144c = fPp.fPd[k];
			if (c144c.fc)
				c144c.mPa(mModel, mVertices, mTextureCoords);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
        float f = (float)width * FPo;
        float f1 = (float)height * FPo;
        fPp.fPa.mPa(-f, f, -f1, f1, FPl, FPm);
        fx.mPa(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		final Resources resources = mContext.getResources();
		String vertexShaderCode = loadAsset(resources, R.raw.vertex);
		String fragmentShaderCode = loadAsset(resources, R.raw.fragment);
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int j = 0;
		if (vertexShader != 0) {
			int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			if (fragmentShader != 0) {
				int program = GLES20.glCreateProgram();
				if (program != 0) {
					GLES20.glAttachShader(program, vertexShader);
					checkGlError("glAttachShader");
					GLES20.glAttachShader(program, fragmentShader);
					checkGlError("glAttachShader");
					GLES20.glLinkProgram(program);
					int params[] = new int[1];
					GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,
							params, 0);
					if (params[0] != 1) {
						Log.e(TAG, "Could not link program: ");
						Log.e(TAG, GLES20.glGetProgramInfoLog(program));
						GLES20.glDeleteProgram(program);
						program = 0;
					}
				} 
				j = program;
			}
		}
		mProgram = j;
		mProjection = GLES20.glGetUniformLocation(mProgram, "uProjection");
		mView = GLES20.glGetUniformLocation(mProgram, "uView");
		mModel = GLES20.glGetUniformLocation(mProgram, "uModel");
		mVertices = GLES20.glGetAttribLocation(mProgram, "aVertices");
		mTextureCoords = GLES20.glGetAttribLocation(mProgram, "aTextureCoords");
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_DITHER);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);
		fx.mPa();
	}

}
