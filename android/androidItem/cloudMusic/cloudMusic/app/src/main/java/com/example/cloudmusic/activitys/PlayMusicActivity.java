package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.test.A;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.views.PlayMusicView;

import org.greenrobot.eventbus.EventBus;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.content.ContentValues.TAG;

public class PlayMusicActivity extends BaseActivity {

    public static final String MUSIC_ID = "musicId";

    private RecyclerView mRv;

    private ImageView mIvBg;
    private PlayMusicView mPlayMusicView;
    private String mMusicId;
    private MusicModel mMusicModel;
    private RealmHelper mRealmHelper;

    private TextView mTvName,mTvAuthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        //隐藏statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initData();
        initView();

    }

    private void initData() {
        mMusicId = getIntent().getStringExtra(MUSIC_ID);
        mRealmHelper = new RealmHelper();
        mMusicModel = mRealmHelper.getMusic(mMusicId);
    }

    private void initView(){

        Integer posotion = getIntent().getIntExtra("position",-1);
        A a = new A();
        a.a = 2;
        a.b = "hello";
        EventBus.getDefault().post(MessageWrap.getInstance(0,posotion.toString(),a));//EventBus发布消息,用来展示播放状态

        mIvBg = findViewById(R.id.iv_bg);
        mTvAuthor = findViewById(R.id.tv_author);
        mTvName = findViewById(R.id.tv_name);
        //glide-transformations
        if(!TextUtils.isEmpty(mMusicModel.getPoster()))
            Glide.with(this)
                   .load(mMusicModel.getPoster())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,10)))//25是模糊程度，10是宽高是原图片的1/10
                   .into(mIvBg);
        else
            Glide.with(this)
                    .load("https://image.lnstzy.cn/aoaodcom/2019-08/17/201908170750199553.jpg.h700.jpg")
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25,10)))//25是模糊程度，10是宽高是原图片的1/10
                    .into(mIvBg);

        mTvName.setText(mMusicModel.getName());
        mTvAuthor.setText(mMusicModel.getAuthor());

        mPlayMusicView = findViewById(R.id.play_music_view);
//        mPlayMusicView.setMusicIcon(mMusicModel.getPoster());//封面
        mPlayMusicView.setMusic(mMusicModel);
        mPlayMusicView.playMusic();//播放音乐
    }
    /**
     * 后退按钮点击事件
     */
    public void onBackClick(View view){
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayMusicView.destory();
        mRealmHelper.close();
    }
}