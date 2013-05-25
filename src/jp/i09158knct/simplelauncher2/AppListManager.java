package jp.i09158knct.simplelauncher2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListManager {
    private final String PREF_KEY_APPS = "apps";
    private final Context mContext;
    private final SharedPreferences mPrefs;

    public AppListManager(Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!mPrefs.contains(PREF_KEY_APPS)) {
            cacheAllApps();
        }
    }

    public List<String[]> fetchAllApps() {
        String[] arrayOfPackageNameAndMainName = mPrefs.getString(PREF_KEY_APPS, "").split("\n");
        ArrayList<String[]> appList = new ArrayList<String[]>();
        for (String names : arrayOfPackageNameAndMainName) {
            appList.add(names.split("\t"));
        }
        return appList;
    }

    public void cacheAllApps() {
        mPrefs.edit().putString(PREF_KEY_APPS, buildPrefValue()).commit();
    }

    private String buildPrefValue() {
        PackageManager manager = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        return toPrefString(apps);
    }

    private String toPrefString(List<ResolveInfo> apps) {
        StringBuilder builder = new StringBuilder();
        PackageManager prefManager = mContext.getPackageManager();
        for (ResolveInfo app : apps) {
            builder.append(app.activityInfo.packageName);
            builder.append("\t");
            builder.append(app.activityInfo.name);
            builder.append("\t");
            builder.append(app.loadLabel(prefManager));
            builder.append("\n");
        }
        return builder.toString();
    }
}
