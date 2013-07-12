package jp.i09158knct.simplelauncher2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class CategoryListActivity extends ListActivity {

    private AppListManager mAppsManager;
    private List<String> mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mAppsManager = new AppListManager(this);
        mCategories = mAppsManager.getAllCategoryNameList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCategories);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String categoryName = mCategories.get(position);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_KEY_CATEGORY_NAME, categoryName);
        startActivity(intent);
        finish();
    }
}
