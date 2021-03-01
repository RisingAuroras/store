package com.example.cloudmusic.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.cloudmusic.R;
import com.example.cloudmusic.helps.MediaPlayerHelp;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.services.MusicService;

public class PlayMusicView extends FrameLayout {

    private Context mContext;
    private Intent mServiceIntent;
    private MusicService.MusicBind mMusicBind;
    private MusicModel mMusicModel;

    private boolean isPlaying;//音乐是否在播放
    private boolean isBindService;//服务是否绑定
    private View mView;
    private FrameLayout mFlPlayMusic;
    private ImageView mIvIcon,mIvNeedle,mIVplay;

    private Animation mPlayMusicAnim,mPlayNeedleAnim,mStopNeedleAnim;
    public PlayMusicView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) //在api21以上才调用
    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }

    private void init(Context context){
        mContext = context;

        mView = LayoutInflater.from(mContext).inflate(R.layout.play_music ,this,false);
        mFlPlayMusic = mView.findViewById(R.id.fl_play_music);
        mIvIcon = mView.findViewById(R.id.iv_icon);
        mIvNeedle = mView.findViewById(R.id.iv_needle);
        mIVplay = mView.findViewById(R.id.iv_play);

        //光盘点击事件，切换播放状态
        mFlPlayMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                triggle();
            }
        });

        /**
         * 1.定义所需要执行的动画
         *      >1光盘转动的动画
         *      >2指针指向光盘的动画
         *      >3指针离开光盘的动画
         *2. startAnimation
         */

        mPlayMusicAnim = AnimationUtils.loadAnimation(mContext,R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(mContext,R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(mContext,R.anim.stop_needle_anim);

        addView(mView);
    }
    /**
     * 设置MusicModel
     */
    public void setMusic (MusicModel musicModel){
        mMusicModel = musicModel;
        setMusicIcon();
    }
    /**
     * 切换播放状态
     */
     private void triggle(){
        if(isPlaying){
            stopMusic();
        }
        else{
            playMusic();
        }
     }
    /**
     * 播放音乐
     */
    public void playMusic(){
        isPlaying = true;
        mIVplay.setVisibility(View.GONE);
        mFlPlayMusic.startAnimation(mPlayMusicAnim);
        mIvNeedle.startAnimation(mPlayNeedleAnim);

        startMusicService();
    }
    /**
     * 停止播放音乐
     */
    public void stopMusic(){
        isPlaying = false;
        mIVplay.setVisibility(View.VISIBLE);
        mFlPlayMusic.clearAnimation();
//        mPlayMusicAnim.setFillAfter();
        mIvNeedle.startAnimation(mStopNeedleAnim);

        if(mMusicBind != null)
            mMusicBind.stopMusic();
    }
    /**
     * 设置光盘中显示的音乐封面图片
     */
    public void setMusicIcon(){
        Glide.with(mContext)
                .load(mMusicModel.getPoster())
                .into(mIvIcon);
    }
    /**
     * 启动音乐服务
     */
    private void startMusicService(){
        //启动Service
        if(mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(mServiceIntent);
        }
        else {
            mMusicBind.playMusic();
        }
        //绑定Service,判断当前Service是否绑定，未绑定的话就绑定
        if(!isBindService){
            isBindService = true;
            mContext.bindService(mServiceIntent,conn,Context.BIND_AUTO_CREATE);
        }
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBind = (MusicService.MusicBind) service;
            mMusicBind.setMusic(mMusicModel);
            mMusicBind.playMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 解出绑定
     */
    public void destory(){
        //如果已经绑定服务，解除绑定
        if(isBindService){
            isBindService = false;
            mContext.unbindService(conn);
        }
    }

}
