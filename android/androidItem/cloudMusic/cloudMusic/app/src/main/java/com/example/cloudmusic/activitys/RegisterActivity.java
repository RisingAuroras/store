package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.UserUtils;
import com.example.cloudmusic.views.InputView;

public class RegisterActivity extends BaseActivity {

    private InputView mInputPhone,mInputPassword,mInputPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    /**
     * 初始化View
     */
    public void  initView() {
        initNavBar(true,"注册",false);

        mInputPassword = findViewById(R.id.input_password);
        mInputPasswordConfirm = findViewById(R.id.input_password_confirm);
        mInputPhone= findViewById(R.id.input_phone);

    }

    /**
     * 注册按钮点击事件
     * 1、用户输入合法性验证
     *  >1、用户输入的手机号是否合法
     *  >2、用户输入了密码和确定密码，以及两次密码的输入是否相同
     *  >3、用户当前输入的手机号是否已经被注册
     * 2、保存用户输入的手机号和密码(MD5加密密码)
     * @param v
     */
    public void onRegisterClick(View v){
        String phone = mInputPhone.getInputStr();
        String password = mInputPassword.getInputStr();
        String passwordConfirm = mInputPasswordConfirm.getInputStr();

        boolean res = UserUtils.registerUser(this,phone,password,passwordConfirm);

        if(res) onBackPressed();//注册成功，后退到登录页面之中
    }
}