package com.example.cloudmusic.activitys;


import android.content.Intent;
import android.widget.TextView;

import com.example.cloudmusic.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends BaseActivityAbstract {

    private String text;

    @BindView(R.id.tv_text)
    TextView tv_text;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_layout;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        text = intent.getStringExtra("text");
        tv_text.setText(String.format("%s测试界面", text));
    }


}
