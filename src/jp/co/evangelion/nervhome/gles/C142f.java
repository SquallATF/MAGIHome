package jp.co.evangelion.nervhome.gles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

public class C142f {
	static String TAG = C142f.class.getSimpleName();
	public int fPb;

	public static Bitmap MPa(View view) {
		Bitmap bitmap = Bitmap.createBitmap(256, 512, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		canvas.scale(256F / view.getWidth(), 512F / view.getHeight());
		view.setDrawingCacheEnabled(false);
		view.draw(canvas);
		return bitmap;
	}

	public static C142f MPa(Bitmap bitmap) {
		C142f c142f = new C142f();
		// Generate one texture pointer...
		int textures[] = new int[1];
		GLES20.glGenTextures(textures.length, textures, 0);
		// ...and bind it to our array
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
		// Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		c142f.fPb = textures[0];
		bitmap.recycle();
		return c142f;
	}

	public static C142f bindPVRTC(Resources resources, int id) {
		C142f c142f = new C142f();
		InputStream inputStream = resources.openRawResource(id);
		byte[] buffer = new byte[1024000];
		int index = 0;
		try {
			for (;;) {
				int k = inputStream.read(buffer);
				if (k != -1) {
					buffer[index] = (byte) k;
					index += k;					
				} else { break ;}
			}
		} catch (IOException e) {
			Log.e(TAG, "bindPVRTC", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e1) {
			}
		}
		ByteBuffer bytebuffer = ByteBuffer.wrap(buffer);
		bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
		int l = bytebuffer.getInt(0);
		int size = index - l;
		int width = bytebuffer.getInt(4);
		int height = bytebuffer.getInt(8);
		bytebuffer.getInt(16);
		byte[] buff1 = new byte[size];
		Log.d(TAG, "texture size" + size);
		System.arraycopy(buffer, l, buff1, 0, size);
		int[] textures = new int[1];
		GLES20.glGenTextures(textures.length, textures, 0);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glCompressedTexImage2D(GLES20.GL_TEXTURE_2D, 0,
				GLES11Ext.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD, width, height, 0, size,
				ByteBuffer.wrap(buff1));
		c142f.fPb = textures[0];
		return c142f;
	}
}
