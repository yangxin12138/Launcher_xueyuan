package com.twd.launcherxueyuan;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.twd.launcherfuguan.R;
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
    private ImageView icon_youxue;
    private ImageView icon_ketang;

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
        icon_youxue = findViewById(R.id.im_youxue);
        icon_ketang = findViewById(R.id.im_ketang);

        top_file.setOnFocusChangeListener(this::onFocusChange);top_file.setOnClickListener(this::onClick);
        top_setting.setOnFocusChangeListener(this::onFocusChange);top_setting.setOnClickListener(this::onClick);
        icon_youxue.setOnFocusChangeListener(this::onFocusChange);icon_youxue.setOnClickListener(this::onClick);
        icon_ketang.setOnFocusChangeListener(this::onFocusChange);icon_ketang.setOnClickListener(this::onClick);
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

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if (view.getId() == R.id.top_file){//文件
            intent = new Intent();
            String file_package = Utils.readSystemProp("LAUNCHERFG_FILE_PACKAGE");
            String file_className = Utils.readSystemProp("LAUNCHERFG_FILE_CLASS");
            intent.setComponent(new ComponentName(file_package,file_className));
            Log.d("yangxin", "onClick: file_package = "+file_package+",file_className = "+file_className);
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