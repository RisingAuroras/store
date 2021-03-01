package com.example.cloudmusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class WEqualsHImageView extends AppCompatImageView {
    public WEqualsHImageView(Context context) {
        super(context);
    }

    public WEqualsHImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * View通过如下两个变量来测量宽和高的
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);//系统测量
////        获取View宽度
//        int width = MeasureSpec.getSize(widthMeasureSpec);
////        获取View模式,match_parent,warp_content,具体dp
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
    }

    public WEqualsHImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
