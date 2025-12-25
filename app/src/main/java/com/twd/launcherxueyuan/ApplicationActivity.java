package com.twd.launcherxueyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ApplicationActivity extends AppCompatActivity {
    GridView gridView ;
    ApplicationAdapter adapter;
    SharedPreferences sharedPreferences;
    int MyPosition ;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        context =  this;
        initView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }
    private void initView(){
        gridView = findViewById(R.id.gridView);
        PackageManager pm = getPackageManager();
        Intent intentLauncher = new Intent(Intent.ACTION_MAIN,null);
        intentLauncher.addCategory(Intent.CATEGORY_LAUNCHER);
        Intent intentLeanbackLauncher = new Intent(Intent.ACTION_MAIN, null);
        intentLeanbackLauncher.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);


        List<ResolveInfo> installedAppsLauncher  = pm.queryIntentActivities(intentLauncher,0);
        List<ResolveInfo> installedAppsLeanbackLauncher = pm.queryIntentActivities(intentLeanbackLauncher, 0);

        //合并两个列表
        List<ResolveInfo> combinedList = new ArrayList<>();
        Set<String> addedPackageNames = new HashSet<>();

        for (ResolveInfo info : installedAppsLauncher) {
            String packageName = info.activityInfo.packageName;
            if (!addedPackageNames.contains(packageName)){
                combinedList.add(info);
                addedPackageNames.add(packageName);
            }
        }
        for (ResolveInfo info : installedAppsLeanbackLauncher){
            String packageName = info.activityInfo.packageName;
            if (!addedPackageNames.contains(packageName)){
                combinedList.add(info);
                addedPackageNames.add(packageName);
            }
        }

        Log.i("yangxin", "initView: -------初始化已安装应用------");
        //创建一个迭代器用于遍历installedApps列表
        Iterator<ResolveInfo> iterator = combinedList.iterator();

        //遍历installedApps列表，过滤应用
        while (iterator.hasNext()){
            ResolveInfo resolveInfo = iterator.next();
            String packageName = resolveInfo.activityInfo.packageName;
            if ("com.twd.launcherxueyuan".equals(packageName)  || "com.android.tv.settings".equals(packageName) ||
                    "com.android.calendar".equals(packageName) || "com.android.deskclock".equals(packageName) ||
                    "com.android.email".equals(packageName) || "com.android.music".equals(packageName) ||
                    "com.android.soundrecorder".equals(packageName) || "org.codeaurora.gallery".equals(packageName)||
                    "org.codeaurora.snapcam".equals(packageName) || "com.android.calculator2".equals(packageName) ||
                    "com.android.documentsui".equals(packageName) || "com.android.quicksearchbox".equals(packageName) ||
                    "com.example.android.notepad".equals(packageName)){
                iterator.remove();//移除
            }
        }
        adapter = new ApplicationAdapter(this,combinedList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResolveInfo app = combinedList.get(position);
                String packageName = app.activityInfo.packageName;
                String className = app.activityInfo.name;
                try{
                    Intent intent = new Intent();
                    intent.setClassName(packageName,className);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyPosition = (int) adapter.getItemId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
