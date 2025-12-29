package com.twd.launcherxueyuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    TextView tv_password; // 密码管理按钮
    private static final String PASSWORD_KEY = "password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        context =  this;
        initView();
        // 初始化密码管理按钮状态
        updatePasswordButtonText();
        gridView.requestFocus();
        gridView.setSelection(0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        initView();
        updatePasswordButtonText(); // 恢复页面时更新按钮状态
        gridView.requestFocus();
        gridView.setSelection(0);
    }
    private void initView(){
        gridView = findViewById(R.id.gridView);
        tv_password = findViewById(R.id.tv_passwordManager);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        tv_password.setOnClickListener(v -> handlePasswordManagerClick());
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

    //更新密码管理按钮的文字（添加密码/忽略密码）
    private void updatePasswordButtonText(){
        String savedPassword = sharedPreferences.getString(PASSWORD_KEY,null);
        if (savedPassword == null){
            tv_password.setText(getString(R.string.application_add_password));
        }else {
            tv_password.setText(getString(R.string.application_ignore_password));
        }
    }

    //处理密码管理按钮点击事件
    private void handlePasswordManagerClick(){
        String savedPassword = sharedPreferences.getString(PASSWORD_KEY,null);
        if (savedPassword == null){
            //无密码：显示添加密码
            showPasswordDialog(true);
        }else {
            // 有密码：显示移除密码弹窗
            showPasswordDialog(false);
        }
    }
    /**
     * 通用密码弹窗
     * @param isAddMode true=添加密码，false=移除密码
     */
    private void showPasswordDialog(boolean isAddMode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password_input,null);
        builder.setView(dialogView);

        //自定义弹窗标题(根据模式切换)
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        if (isAddMode){
            tvTitle.setText(getString(R.string.dialog_add_title));
        }else {
            tvTitle.setText(getString(R.string.dialog_delete_title));
        }

        EditText etPassword = dialogView.findViewById(R.id.et_password);
        TextView btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        btnConfirm.setOnClickListener(v ->{
            String inputPassword = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(inputPassword)){
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isAddMode){
                //保存密码
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PASSWORD_KEY,inputPassword);
                editor.apply();
                Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
            }else {
                //移除密码逻辑:验证原密码
                String savedPassword = sharedPreferences.getString(PASSWORD_KEY,"");
                if (!inputPassword.equals(savedPassword)){
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    return;
                }
                //删除密码
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(PASSWORD_KEY);
                editor.apply();
                Toast.makeText(this, "密码已移除", Toast.LENGTH_SHORT).show();
            }
            //更新按钮文字并关闭弹窗
            updatePasswordButtonText();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener( v -> dialog.dismiss());
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
