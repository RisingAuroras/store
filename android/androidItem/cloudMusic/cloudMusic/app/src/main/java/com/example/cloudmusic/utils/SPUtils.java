package com.example.cloudmusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cloudmusic.constants.SPConstants;
import com.example.cloudmusic.helps.UserHelper;
/**
 * sharepref工具类
 *
 * @author llw
 */
public class SPUtils {
    private static final String NAME = "config";
    /**
     * 当用户登录时，利用SharedPreferences 保存登录用户的用户标记(手机号码)
     */
    public static boolean saveUser(Context context,String phone){
        SharedPreferences sp = context.getSharedPreferences(SPConstants.SP_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(SPConstants.SP_KEY_PHONE,phone);
        boolean res = edit.commit();
        return res;

    }

    /**
     * 验证是否存在已登录用户
     */
    public static boolean isLoginUser(Context context){
        boolean res = false;

        SharedPreferences sp = context.getSharedPreferences(SPConstants.SP_NAME_USER, Context.MODE_PRIVATE);
        String phone = sp.getString(SPConstants.SP_KEY_PHONE, "");

        if(!TextUtils.isEmpty(phone)){
            res = true;
            UserHelper.getInstance().setPhone(phone);
        }
        return res;
    }

    /**
     * 删除用户标记
     */
    public static boolean removeUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(SPConstants.SP_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(SPConstants.SP_KEY_PHONE);
        boolean res = editor.commit();
        return res;
    }


    public static void putBoolean(String key, boolean value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue, Context context) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }
        return "";

    }

    public static void putInt(String key, int value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(String key, int defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void remove(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
}
