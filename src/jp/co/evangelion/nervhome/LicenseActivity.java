package jp.co.evangelion.nervhome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class LicenseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license);
		final TextView textView = (TextView) findViewById(R.id.tv_license);
		final Handler handler = new Handler();
		new Thread(new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(
							getAssets().open("apache2_0.txt")));
					while (true) {
						String line = reader.readLine();
						if (line == null) {
							break;
						}
						sb.append(line + "\n");
					}
					reader.close();
					final String license = sb.toString();
					handler.post(new Runnable() {

						@Override
						public void run() {
							textView.setText(license);
						}
					});
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}).start();
	}
}
