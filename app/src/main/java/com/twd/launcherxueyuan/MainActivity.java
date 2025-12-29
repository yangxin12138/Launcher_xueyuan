package com.twd.launcherxueyuan;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.launcherxueyuan.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tv_day;
    private TextView tv_time;
    private Handler timerHandler = new Handler();

    private TextView top_file;
    private TextView top_setting;
    private TextView top_applications;
    private ImageView icon_youxue;
    private ImageView icon_ketang;
    // 在MainActivity类中添加成员变量
    private SharedPreferences sp;
    private static final String PASSWORD_KEY = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        updateTimeRunnable.run();
    }

    private void initView(){
        tv_day = findViewById(R.id.tv_day);
        tv_time = findViewById(R.id.tv_time);
        top_file = findViewById(R.id.top_file);
        top_setting = findViewById(R.id.top_setting);
        top_applications = findViewById(R.id.top_applications);
        icon_youxue = findViewById(R.id.im_youxue);
        icon_ketang = findViewById(R.id.im_ketang);
        sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        setPassword("314159");
        top_file.setOnFocusChangeListener(this::onFocusChange);top_file.setOnClickListener(this::onClick);
        top_setting.setOnFocusChangeListener(this::onFocusChange);top_setting.setOnClickListener(this::onClick);
        top_applications.setOnFocusChangeListener(this::onFocusChange);
        top_applications.setOnClickListener(v -> checkPasswordAndOpenApplications());
        icon_youxue.setOnFocusChangeListener(this::onFocusChange);icon_youxue.setOnClickListener(this::onClick);
        icon_ketang.setOnFocusChangeListener(this::onFocusChange);icon_ketang.setOnClickListener(this::onClick);
    }

    private void checkPasswordAndOpenApplications(){
        String savedPassword = sp.getString(PASSWORD_KEY,null);
        if (savedPassword == null){
            openApplicationActivity();
        }else {
            showPasswordDialog(savedPassword);
        }
    }

    private void openApplicationActivity() {
        Intent intent = new Intent(MainActivity.this, ApplicationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void showPasswordDialog(String savedPassword){
        //创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password_input, null);
        builder.setView(dialogView);

        EditText etPassword = dialogView.findViewById(R.id.et_password);
        TextView btnConfirm = dialogView.findViewById(R.id.btn_confirm); // 自定义确定按钮
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);   // 自定义取消按钮
        // 4. 创建对话框并设置属性
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true); // 允许返回键取消
        dialog.setCanceledOnTouchOutside(false); // 不允许点击外部关闭

        // 5. 绑定确定按钮点击事件
        btnConfirm.setOnClickListener(v -> {
            String inputPassword = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(inputPassword)) {
                Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (inputPassword.equals(savedPassword)) {
                // 密码正确，打开页面并关闭对话框
                openApplicationActivity();
                dialog.dismiss();
            } else {
                // 密码错误，提示并清空输入框
                Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                etPassword.setText(""); // 清空输入框
            }
        });

        // 6. 绑定取消按钮点击事件
        btnCancel.setOnClickListener(v -> dialog.dismiss()); // 关闭对话框

        // 7. 显示对话框并调整样式
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
            // 去除Dialog默认背景，显示自定义浅蓝色背景
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setPassword(String password) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            getSystemTime();
            //每隔一秒更新一次时间
            timerHandler.postDelayed(this,1000);
        }
    };
    private void getSystemTime(){
        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();


        //设置日期的格式
        TimeZone timeZone = calendar.getTimeZone();
        DateFormat dateFormat;
        if ("Asia/Shanghai".equals(timeZone.getID())){
            dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        }else {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }
        String formatterDate = dateFormat.format(currentDate);

        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate);
        formatterDate = formatterDate+"\n"+dayOfWeek;

        String timeFormatString = Utils.getTimeFormat(this);
        //设置时间的格式
        DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
        String formatterTime = timeFormat.format(currentDate);

        //在TextView上更新日期和时间
        tv_day.setText(formatterDate);
        tv_time.setText(formatterTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimeRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if (view.getId() == R.id.top_file){//文件
            intent = new Intent();
            String file_package = Utils.readSystemProp("LAUNCHERFG_FILE_PACKAGE");
            String file_className = Utils.readSystemProp("LAUNCHERFG_FILE_CLASS");
            intent.setComponent(new ComponentName(file_package,file_className));
            Log.d("yangxin", "onClick: file_package = "+file_package+",file_className = "+file_className);
        }else if (view.getId() == R.id.top_applications) {//我的应用
            intent = new Intent(MainActivity.this,ApplicationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (view.getId() == R.id.top_setting) {//设置
            intent = new Intent();
            String setting_package = Utils.readSystemProp("LAUNCHERFG_SETTING_PACKAGE");
            String setting_className = Utils.readSystemProp("LAUNCHERFG_SETTING_CLASS");
            intent.setComponent(new ComponentName(setting_package,setting_className));
            Log.d("yangxin", "onClick: setting_package = "+setting_package+",setting_className = "+setting_className);
        } else if (view.getId() == R.id.im_youxue) {//游学app
            intent = getPackageManager().getLaunchIntentForPackage("uni.UNI7E6783A");
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else if (view.getId() == R.id.im_ketang) { // 课堂
            intent = new Intent();
            intent.setComponent(new ComponentName("com.jxw.dpds","com.jxw.dpds.ui.activity.MainActivity"));
        }

        if (intent != null){
            try {
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "应用不存在", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus){
            view.animate().scaleX(1.1f).scaleY(1.1f).translationZ(1f).setDuration(100);
        }else {
            view.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(100);
        }
    }
}