package jp.i09158knct.simplelauncher2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

public class MainActivity extends Activity {

    public static final String EXTRA_KEY_CATEGORY_NAME = "category";
    private AppListManager mAppsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String categoryName = extractCategoryNameFromIntentOrDefault();

        mAppsManager = new AppListManager(this);
        List<String[]> appInfos = mAppsManager.getCategory(categoryName);
        initializeGridView(appInfos);
    }

    private String extractCategoryNameFromIntentOrDefault() {
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra(EXTRA_KEY_CATEGORY_NAME);
        return (categoryName == null) ?
                AppListManager.CATEGORY_NAME_ALL_APPS :
                categoryName;
    }

    private void initializeGridView(final List<String[]> appInfos) {
        GridView grid = (GridView) findViewById(R.id.main_grid);
        AppInfosAdopter adapter = new AppInfosAdopter(this, appInfos);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = AppListManager.createLaunchIntent(appInfos.get(position));
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mAppsManager.cacheAllApps();
                Toast.makeText(this, "List has been updated.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_new_category:
                intent = new Intent(this, NewCategoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_select_category:
                intent = new Intent(this, CategoryListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
