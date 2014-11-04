/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.evangelion.nervhome;

import android.graphics.drawable.Drawable;
import android.graphics.PixelFormat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

class FastBitmapDrawable extends Drawable {
	private Bitmap mBitmap;
	private Paint mPaint;

	FastBitmapDrawable(Bitmap b) {
		mBitmap = b;
	}

	@Override
	public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap,  null, getBounds(), mPaint);
	}

	@Override
	public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		if(mPaint == null) {
			mPaint = new Paint();
		}
		mPaint.setColorFilter(cf);
	}

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public int getMinimumWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getMinimumHeight() {
        return mBitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
