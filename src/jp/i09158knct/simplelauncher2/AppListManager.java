package jp.i09158knct.simplelauncher2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.*;

public class AppListManager {
    static private final String SPECIAL_KEY_ALL_APP = "__all__";
    static private final String PREF_KEY_CATEGORY_SOLT = "category_";
    static public final String CATEGORY_NAME_ALL_APPS = SPECIAL_KEY_ALL_APP;
    private final Context mContext;
    private final SharedPreferences mPrefs;

    public AppListManager(Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!mPrefs.contains(convertToPrefKey(SPECIAL_KEY_ALL_APP))) {
            cacheAllApps();
        }
    }

    static public Intent createLaunchIntent(String[] appInfo) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setClassName(appInfo[0], appInfo[1]);
        return intent;
    }

    public List<String[]> getCategoryOfAllApps() {
        return getCategory(SPECIAL_KEY_ALL_APP);
    }

    public void cacheAllApps() {
        List<ResolveInfo> apps = getResolveInfoList();
        List<String[]> appInfos = convertResolveInfoListToAppInfoList(apps);
        saveCategory(SPECIAL_KEY_ALL_APP, appInfos);
    }

    public List<String> getAllCategoryNameList() {
        Set<String> prefKeySet = mPrefs.getAll().keySet();
        ArrayList<String> categories = new ArrayList<String>();
        for (String prefKey : prefKeySet) {
            if (prefKey.startsWith(PREF_KEY_CATEGORY_SOLT)) {
                categories.add(prefKey.substring(PREF_KEY_CATEGORY_SOLT.length()));
            }
        }
        return categories;
    }

    public List<String[]> getCategory(String categoryName) {
        String[] rows = mPrefs.getString(convertToPrefKey(categoryName), "").split("\n");
        ArrayList<String[]> appInfos = new ArrayList<String[]>(rows.length);
        for (String names : rows) {
            appInfos.add(names.split("\t"));
        }
        return appInfos;
    }

    public void saveCategory(String categoryName, List<String[]> appInfos) {
        String value = convertAppInfoListToString(appInfos);
        mPrefs.edit().putString(convertToPrefKey(categoryName), value).commit();
    }

    private List<ResolveInfo> getResolveInfoList() {
        PackageManager manager = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        return apps;
    }

    private String convertToPrefKey(String categoryName) {
        return PREF_KEY_CATEGORY_SOLT + categoryName;
    }

    private String convertAppInfoListToString(List<String[]> apps) {
        StringBuilder builder = new StringBuilder();
        for (String[] app : apps) {
            builder.append(app[0]);
            builder.append("\t");
            builder.append(app[1]);
            builder.append("\t");
            builder.append(app[2]);
            builder.append("\n");
        }
        return builder.toString();
    }

    private List<String[]> convertResolveInfoListToAppInfoList(List<ResolveInfo> apps) {
        ArrayList<String[]> appInfoList = new ArrayList<String[]>(apps.size());
        PackageManager prefManager = mContext.getPackageManager();
        for (ResolveInfo app : apps) {
            String[] appInfo = new String[3];
            appInfo[0] = app.activityInfo.packageName;
            appInfo[1] = app.activityInfo.name;
            appInfo[2] = app.loadLabel(prefManager).toString();
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

}
