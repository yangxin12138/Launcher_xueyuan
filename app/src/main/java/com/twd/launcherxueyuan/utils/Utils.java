package com.twd.launcherxueyuan.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

import java.lang.reflect.Method;
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

    public static String getSerialNumber() {
        String serial = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Q 及以上版本
            Log.d("yangxin", "getSerialNumber: -----Android Q 以上");
            serial = Build.getSerial();
        } else {
            // Android Q 之前
            Log.d("yangxin", "getSerialNumber: -----Android Q 以下");
            serial = Build.SERIAL;
        }
        return serial;
    }

    public static String getMac(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        Log.d("yangxin", "getMac: macAddress = "+ macAddress);
        if (macAddress != null){
            macAddress = macAddress.toUpperCase();
        }
        return macAddress;
    }
}
