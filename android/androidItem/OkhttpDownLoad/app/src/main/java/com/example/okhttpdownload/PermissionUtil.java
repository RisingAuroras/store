package com.example.okhttpdownload;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    public static String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;     //内存读取
    public static String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;   //内存写入

    public static void getPermissions(Activity activity, String... permission) {

        List<String> permissions = new ArrayList<>();
        //此处做动态权限申请
        //判断系统是否大于等于Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(int i=0;i<permission.length;i++){
                int request = ContextCompat.checkSelfPermission(activity, permission[i]);
                //判断是否未获取权限
                if (request != PackageManager.PERMISSION_GRANTED)
                    permissions.add(permission[i]);
            }
            if (permissions.size()>0) {//缺少权限，进行权限申请
                //当前上下文;一个权限数组;一个唯一的请求码(0~65535的16位数)
                ActivityCompat.requestPermissions(activity,  permissions.toArray(new String[permissions.size()]), 0XFF);
            } else {
                //权限同意 已全部授权
            }
        } else {
            //低于api 23 不需要动态授权
        }
    }

}
