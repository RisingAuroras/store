package com.example.cloudmusic;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.blankj.utilcode.util.Utils;
import com.example.cloudmusic.helps.RealmHelper;

import org.litepal.LitePal;

import io.realm.Realm;

public class MyApplication extends Application {
    private static MyApplication myApplication;

    public static MyApplication getInstance() {
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化AndroidUtilCode
        Utils.init(this);
        //初始化Realm
        Realm.init(this);
        //检测Realm是否需要数据迁移
        RealmHelper.migration();

        LitePal.initialize(this);

        myApplication = this;
//        initTextSize();
    }

    /**
     * 使其系统更改字体大小无效
     */
    private void initTextSize() {
        Resources res = getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
