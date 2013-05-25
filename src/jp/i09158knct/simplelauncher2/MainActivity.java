package jp.i09158knct.simplelauncher2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

public class MainActivity extends Activity {

    private AppListManager mAppsManager;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mAppsManager = new AppListManager(this);
		List<String[]> appInfos = mAppsManager.fetchAllApps();
		initializeGridView(appInfos);
	}

	private void initializeGridView(final List<String[]> appInfos) {
		GridView grid = (GridView) findViewById(R.id.main_grid);
		AppInfosAdopter adapter = new AppInfosAdopter(this, appInfos);
		grid.setAdapter(adapter);
		grid.setSelection(0);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String[] appInfo = appInfos.get(position);
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				intent.setClassName(appInfo[0], appInfo[1]);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mAppsManager.cacheAllApps();
		Toast.makeText(this, "List has been updated.", Toast.LENGTH_SHORT).show();
		return true;
	}

	private class AppInfosAdopter extends ArrayAdapter<String[]> {
		private List<String[]> mAppInfos;

		public AppInfosAdopter(Context context, List<String[]> appInfos) {
			super(context, 0, appInfos);
			mAppInfos = appInfos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String[] appInfo = mAppInfos.get(position);
			String appname = appInfo[2];

			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.item_app, parent, false);
			}

			TextView textView = (TextView) convertView.findViewById(R.id.app_label);
			textView.setText(appname);
			return convertView;
		}
	}

}
