package com.twd.launcherxueyuan.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class Utils {
    private static final String CLASS_NAME = "android.os.SystemProperties";
    public static String getTimeFormat(Context context){
        //获取当前local
        Locale locale = Locale.getDefault();

        //检查是否是24小时制
        if (DateFormat.is24HourFormat(context)){
            return "HH:mm";
        } else {
            return "hh:mm a";
        }
    }

    public static String readSystemProp(String search_line) {
        String line = "";
        try {
            File file = new File("/system/etc/settings.ini");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                if (line.contains(search_line)) {
                    // 这里可以进一步解析line来获取STORAGE_SIMPLE_SYSDATA的值
                    String value = line.split("=")[1].trim(); // 获取等号后面的值
                    reader.close();
                    fis.close();
                    return value;
                }
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Standard";
    }
}
