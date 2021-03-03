package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.UserUtils;
import com.example.cloudmusic.views.InputView;

public class LoginActivity extends BaseActivity {

    private InputView mInputPhone,mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        initNavBar(false,"登录",false);

        mInputPhone = findViewById(R.id.input_phone);
        mInputPassword = findViewById(R.id.input_password);
    }
    /**
     * 跳转到注册页面点击事件
     */
    public  void onRegisterClick(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 登录按钮点击事件
     */
    public void onCommiterClick(View v){
        String phone = mInputPhone.getInputStr();
        String password = mInputPassword.getInputStr();
        //验证用户输入是否合法
        if(!UserUtils.validateLogin(this,phone,password)){
            return ;
        }

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();

    }
}