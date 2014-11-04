package jp.co.evangelion.nervhome.icons;

import java.util.ArrayList;

import jp.co.evangelion.nervhome.CustomShirtcutActivity;
import jp.co.evangelion.nervhome.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class DefaultIcons extends Activity implements OnItemClickListener {
	  public static final ColorMatrixColorFilter mFilter = new ColorMatrixColorFilter(new float[] { 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.3F, 0.6F, 0.0F, 0.0F, 0.0F, 0.2F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F });
	  private final ViewGroup.LayoutParams mLayoutParams = new AbsListView.LayoutParams(144, 144);
	  private GridView mIconGrid;
	  private boolean mFlagd = false;
	  private final AsyncTask mTask = new MyTask();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.icons);
		mIconGrid = (GridView)findViewById(R.id.icon_grid);
		mIconGrid.setColumnWidth(5);
	    mIconGrid.setGravity(17);
	    mIconGrid.setOnItemClickListener(this);
	    mTask.execute();
	    if (getIntent().getAction().equals(CustomShirtcutActivity.ACTION_ADW_PICK_ICON)) {
	        this.mFlagd = true;
	      }
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mFlagd) {
			Intent intent = getIntent();
			if(position == 0){
				intent.putExtra("origin_customed", false);
				setResult(RESULT_OK,intent);
			} else {
				intent.putExtra("origin_customed", true);
				Bitmap bitmap;
				try {
					bitmap = (Bitmap)parent.getAdapter().getItem(position);
				} catch (Exception e) {
					bitmap = null;
				}
				if (bitmap != null) {
					intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
					setResult(RESULT_OK, intent);
				} else {
					setResult(RESULT_CANCELED, intent);
				}
			}
			finish();
		}

	}
	
	Bitmap Ma(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Bitmap bitmap1 = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			Paint paint = new Paint();
			paint.setColorFilter(mFilter);
			final Canvas canvas = new Canvas(bitmap1);
			canvas.drawBitmap(bitmap, null, new Rect(0, 0, width, height),
					paint);
			return bitmap1;
		}
		return null;
	}

	class MyTask extends AsyncTask<Object, Void, Object> {
		private MyAdapter mAdapter;

		@Override
		protected Object doInBackground(Object... params) {
			mAdapter = new MyAdapter(DefaultIcons.this);
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mIconGrid.setAdapter(mAdapter);
		}
	}
	
	class MyAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Integer> fpc = new ArrayList<Integer>();
		private Drawable fpd;
		private Bitmap fpe;
		private int fpf;

		public MyAdapter(Context context) {
			super();
			fpf = 0;
			mContext = context;
			final Bundle bundle = getIntent().getExtras();
			Intent intent = (Intent)bundle.getParcelable(Intent.EXTRA_SHORTCUT_INTENT);
			if ( intent != null) {
				if (CustomShirtcutActivity.ACTION_LAUNCHERACTION.equals(intent.getAction())) {
					fpd = getResources().getDrawable(bundle.getInt("launcher_action_icon_id"));
				} else {
					final PackageManager pm = getPackageManager();
					try {
						fpd = pm.getActivityIcon(intent);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			if (fpd != null) {
				fpf = fpf+1;
				fpe = Ma(fpd);
				if (fpe != null) {
					fpf = fpf +1;
				}
			}
			mpa(getResources(), getApplication().getPackageName(), R.array.icon_pack);
		}

		private void mpa(Resources resources, String packageName, int id) {
			for (String name : resources.getStringArray(id)) {
				if (resources.getIdentifier(name, "drawable", packageName) != 0) {
					int resId = resources.getIdentifier(name, "drawable",
							packageName);
					if (resId != 0) {
						fpc.add(resId);
					}
				}
			}
		}

		@Override
		public int getCount() {
			return fpc.size() + fpf;
		}

		@Override
		public Object getItem(int index) {
			if (index ==0 && fpd !=null) {
				if(fpd instanceof BitmapDrawable) {
					return ((BitmapDrawable)fpd).getBitmap();
				}
				return null;
			}
			if (index == 1 && fpe != null) {
				return fpd;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return BitmapFactory.decodeResource(mContext.getResources(), fpc.get(index - fpf));
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ImageView imageView;
			if(v == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(mLayoutParams);
				imageView.setPadding(24, 24, 24, 24);
			} else {
				imageView =(ImageView)v;
			}
			if(position ==0&& fpd!=null) {
				imageView.setImageDrawable(fpd);
				return imageView;
			}
			if(position ==1 && fpd!=null) {
				imageView.setImageBitmap(fpe);
				return imageView;
			}
			imageView.setImageResource(fpc.get(position - fpf));
			return imageView;
		}
		
	}
	
}
