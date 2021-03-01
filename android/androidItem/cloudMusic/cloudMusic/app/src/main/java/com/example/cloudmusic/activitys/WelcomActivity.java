package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.test.test;
import com.example.cloudmusic.utils.UserUtils;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomActivity extends BaseActivity {
    private Timer mTimer;

    /**
     * 位移动画
     */
    private TranslateAnimation translateAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        init();
    }

    /**
     * 初始化 ，睡眠3s
     */
    private void init() {
        final boolean isLogin = UserUtils.validateUserLogin(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                Log.e("WelcomeActivity","当前线程为" + Thread.currentThread());
                if(false){
                    toHome();
                }
                else toLogin();
            }
        },1*1600);


    }

    /**
     * 跳转到MainActivity
     */
    private void toMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * 跳转到LoginActivity
     */
    private void toLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到HomeActivity
     */
    private void toHome(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * 跳转到test
     */
    private void totest(){
        Intent intent = new Intent(this, test.class);
        startActivity(intent);
        finish();
    }
}