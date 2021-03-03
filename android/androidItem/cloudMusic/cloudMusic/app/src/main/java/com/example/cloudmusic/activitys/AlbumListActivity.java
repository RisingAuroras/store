package com.example.cloudmusic.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.cloudmusic.R;
import com.example.cloudmusic.adapters.MusicListAdapter;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.AlbumModel;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.services.MusicService;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.utils.SPUtils;
import com.example.cloudmusic.utils.StaticProgressMemo;
import com.example.cloudmusic.utils.ToastUtil;
import com.example.cloudmusic.views.MusicRoundProgressView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AlbumListActivity extends BaseActivity {

    public static final String ALBUM_ID = "albumId";

    private final String TAG = "AlbumListActivity";


    public int musicState = 0; // 0 为 不播放 ， 1 为播放
    /**
     * 图片动画
     */
    private ObjectAnimator logoAnimation;

    /**
     * 自定义进度条
     */
    private MusicRoundProgressView musicProgress;

    /**
     * 音乐进度间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    private long exitTime = 0;
    /**
     * 时间
     */
    private View playControlView;//底部控制视图
    /**
     * 底部logo图标，点击之后弹出当前播放歌曲详情页
     */
    private ShapeableImageView ivLogo;
    /**
     * 底部当前播放歌名
     */
    private MaterialTextView tvDefaultName;

    private MusicService.MusicBind musicBinder;
    /**
     * 底部当前歌曲控制按钮, 播放和暂停
     */
    private ImageView btnPlay;
    /**
     * 底部当前歌曲控制 上一首
     */
    private ImageView btnPrev;
    /**
     * 底部当前歌曲控制 下一首
     */
    private ImageView btnNext;

    private List<MusicModel> musicList;
    /**
     * 列表位置
     */
    private int listPosition = 0;
    /**
     * 上下文参数
     */
    protected Activity context;
    private RecyclerView mRvList;
    private MusicListAdapter mAdapter;
    private String mAlbumId;
    private RealmHelper mRealmHelper;
    private AlbumModel mAlbumModel;
    private int oldPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅


        initData();
        initView();
    }

    private void initData() {
        initAnimation();//底部转动动画
        mAlbumId = getIntent().getStringExtra(ALBUM_ID);
        mRealmHelper = new RealmHelper();
        mAlbumModel = mRealmHelper.getAlbum(mAlbumId);
    }

    private void initView() {
        initNavBar(true,"专辑列表",false);

        ivLogo = findViewById(R.id.play_control_layout).findViewById(R.id.iv_logo);
        tvDefaultName = findViewById(R.id.play_control_layout).findViewById(R.id.tv_default_name);
        btnPlay =findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_play);
        btnPrev = findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_pre);
        btnNext = findViewById(R.id.play_control_layout).findViewById(R.id.lock_music_next);
        musicProgress = findViewById(R.id.play_control_layout).findViewById(R.id.music_progress);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicState ^= 1;

                if(musicState == 1){
                    btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                    EventBus.getDefault().post(MessageWrap.
                            getInstance(0,"PlayMusic",null));//EventBus发布消息,用来展示播放状态
                }
                else {
                    btnPlay.setImageResource(R.drawable.play_black);
                    EventBus.getDefault().post(MessageWrap.
                            getInstance(0,"PauseMusic",null));//EventBus发布消息,用来展示播放状态
                }


            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(MessageWrap.getInstance(0,"PrevMusic",null));//EventBus发布消息,用来展示播放状态

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(MessageWrap.getInstance(0,"NextMusic",null));//EventBus发布消息,用来展示播放状态
            }
        });




        mRvList = findViewById(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mAdapter = new MusicListAdapter(this,null,mAlbumModel.getList());
        mRvList.setAdapter(mAdapter);
    }
    public boolean isServiceExisted(String className) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 初始化动画
     */
    private void initAnimation() {
        logoAnimation = ObjectAnimator.ofFloat(ivLogo, "rotation", 0.0f, 360.0f);
        logoAnimation.setDuration(10000);
        logoAnimation.setInterpolator(new LinearInterpolator());
        logoAnimation.setRepeatCount(-1);
        logoAnimation.setRepeatMode(ObjectAnimator.RESTART);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageWrap event){
        if(event.getMessage().equals("musicList")){
            listPosition = event.getInteger();
//            Log.d(TAG,"位置 " + listPosition);
            musicList = (List<MusicModel>) event.getObject();

            MusicModel model = musicList.get(listPosition);

            Glide.with(context).load(model.getPoster())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivLogo);
            tvDefaultName.setText(model.getName());
            //图片旋转动画
            logoAnimation.start();
            musicState = 1;
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        }
        else if(event.getMessage().equals("changeMusic")){ //改变音乐

            oldPosition = listPosition;

            listPosition = event.getInteger();
            if(listPosition == oldPosition) return;

            StaticProgressMemo.setPos(listPosition);
            SPUtils.putString("lastMusicId",musicList.get(listPosition).getMusicId(),this);

            if(oldPosition != -1)
                new RealmHelper().changeMusicModelCheckIsFalse(musicList.get(oldPosition).getMusicId());
            if(listPosition != -1)
                new RealmHelper().changeMusicModelCheckIsTrue(musicList.get(listPosition).getMusicId());

            mAdapter.changeState();


            listPosition = event.getInteger();
            MusicModel model = musicList.get(listPosition);

            Glide.with(context).load(model.getPoster())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(ivLogo);
            tvDefaultName.setText(model.getName());
            //图片旋转动画
            logoAnimation.start();
            musicState = 1;
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);

        }
        else if(event.getMessage().equals("AnswerMusicProgress")){
            int progress = event.getInteger();
            int total = (Integer)event.getObject();
            Log.d(TAG,progress + " " + total);
            if(progress >= total-1000){//播完这首，发送 next 请求
                EventBus.getDefault().post(MessageWrap.getInstance(0,"NextMusic",null));//EventBus发布消息,用来展示播放状态

            }
            musicProgress.setProgress(progress, total);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelper.close();
        EventBus.getDefault().unregister(this);//取消订阅

    }
}