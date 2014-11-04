package jp.co.evangelion.nervhome.selector;

import jp.co.evangelion.nervhome.gles.C138j;
import jp.co.evangelion.nervhome.gles.C139i;
import jp.co.evangelion.nervhome.gles.C146e;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
//import jp.co.sharp.android.stereo3dlcd.SurfaceController;

public class StereoScreenSelector extends ScreenSelector implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	//SurfaceController mSurfaceController = new SurfaceController(this);

	public StereoScreenSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		setStereoView(sharedPreferences.getBoolean("screen_stereo", false));
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("screen_stereo")) {
			setStereoView(sharedPreferences.getBoolean("screen_stereo", true));
		}
		
	}

	@Override
	protected C139i createRenderer(C138j c138j) {
		fc = new C146e(getContext(), c138j);
		return fc;
	}

	public void setStereoView(boolean y) {
		((C146e) fc).fPc = y;
		//mSurfaceController.setStereoView(y);		
	}

}
