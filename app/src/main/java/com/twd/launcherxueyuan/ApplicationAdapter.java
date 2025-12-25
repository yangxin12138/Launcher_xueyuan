package com.twd.launcherxueyuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 16:55 2024/5/21
 */
public class ApplicationAdapter extends BaseAdapter {
    private Context context;
    private List<ResolveInfo> appList;
    public ApplicationAdapter(Context context, List<ResolveInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResolveInfo app = appList.get(position);
        PackageManager manager = context.getPackageManager();
        ViewHold viewHold = null;
        if (convertView == null){
            convertView = View.inflate(context,R.layout.index_item_layout,null);
            viewHold = new ViewHold();
            viewHold.tv_name = convertView.findViewById(R.id.appName);
            viewHold.iv_icon = convertView.findViewById(R.id.appIcon);
            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.packageName = app.activityInfo.packageName;

        try{
            ApplicationInfo applicationInfo = manager.getApplicationInfo(viewHold.packageName,0);
            String appName = (String) applicationInfo.loadLabel(manager);
            viewHold.tv_name.setText(appName);
            viewHold.iv_icon.setImageDrawable(app.activityInfo.loadIcon(manager));
        }catch (PackageManager.NameNotFoundException e){e.printStackTrace();}
        return convertView;
    }

    public class ViewHold {
        public void setTv_name(TextView tv_name) {
            this.tv_name = tv_name;
        }

        public TextView tv_name;
        public ImageView iv_icon;
        public ImageView iv_red;
        public String packageName;
    }
}
