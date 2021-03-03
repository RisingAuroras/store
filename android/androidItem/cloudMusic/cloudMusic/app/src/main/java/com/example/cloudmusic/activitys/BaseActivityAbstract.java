package com.example.cloudmusic.activitys;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cloudmusic.R;

/**
 * @function: 所有Activity的基类，用来处理一些公共事件，如：数据统计
 */
public abstract class BaseActivityAbstract extends AppCompatActivity {

    private ImageView mIvBack,mIvMe;
    private TextView mTvTitle;

    public Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
    }
    /**
     * 初始化NavigationBar
     * @param isShowBack
     * @param title
     * @param isShowMe
     */
    protected void initNavBar(boolean isShowBack,String title,boolean isShowMe){
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mIvMe = findViewById(R.id.iv_me);

        mIvBack.setVisibility(isShowBack ? View.VISIBLE : View.GONE);
        mIvMe.setVisibility(isShowMe ? View.VISIBLE : View.GONE);
        mTvTitle.setText(title);

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mIvMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivityAbstract.this,MeActivity.class));
            }
        });
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
