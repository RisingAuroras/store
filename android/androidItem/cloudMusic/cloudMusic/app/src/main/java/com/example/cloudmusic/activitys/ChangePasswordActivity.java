package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.utils.UserUtils;
import com.example.cloudmusic.views.InputView;

public class ChangePasswordActivity extends BaseActivity {

    private InputView mOldPassword, mPassword,mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    private void initView() {
        initNavBar(true,"修改密码",false);

        mOldPassword = findViewById(R.id.input_old_password);
        mPassword = findViewById(R.id.input_new_password);
        mPasswordConfirm = findViewById(R.id.input_new_password_confirm);
    }

    public void onChangePasswordClick(View v){
        String oldPassword = mOldPassword.getInputStr();
        String password = mPassword.getInputStr();
        String passwordConfirm = mPasswordConfirm.getInputStr();

        boolean res = UserUtils.changePassword(this,oldPassword, password, passwordConfirm);
        if(!res){
//            Toast.makeText(this, "系统错误，修改失败", Toast.LENGTH_SHORT).show();
            return ;
        }

        UserUtils.logout(this);
    }
}