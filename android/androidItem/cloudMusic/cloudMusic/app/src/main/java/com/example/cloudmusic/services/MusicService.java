package com.example.cloudmusic.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activitys.WelcomActivity;
import com.example.cloudmusic.constants.Constant;
import com.example.cloudmusic.helps.MediaPlayerHelp;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.livedata.LiveDataBus;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.Song;
import com.example.cloudmusic.receiver.NotificationClickReceiver;
import com.example.cloudmusic.utils.BLog;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;
import com.example.cloudmusic.utils.SPUtils;
import com.example.cloudmusic.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static android.app.Notification.VISIBILITY_SECRET;
import static com.example.cloudmusic.constants.Constant.PAUSE;
import static com.example.cloudmusic.constants.Constant.PROGRESS;
import static com.example.cloudmusic.constants.MusicConstant.CLOSE;
import static com.example.cloudmusic.constants.MusicConstant.NEXT;
import static com.example.cloudmusic.constants.MusicConstant.PLAY;
import static com.example.cloudmusic.constants.MusicConstant.PREV;

/**
 * 1、通过Service 连接 PlayMusicView 和 MediaPlayHelper
 * 2、PlayMusicView --> Service:
 *      >1、播放音乐、暂停音乐
 *      >2、启动Service ，绑定Service、解出绑定Service
 * 3、MediaPlayerHelper <-- Service
 *      >1、播放音乐、暂停音乐
 *      >2、监听音乐播放完成方法，停止Service
 */
public class MusicService extends LifecycleService implements MediaPlayer.OnCompletionListener {


    private static final String TAG = "MusicService";

    /**
     * 歌曲间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    /**
     * 歌曲列表
     */
    private static List<Song> mList = new ArrayList<>();

    /**
     * 音乐播放器
     */
    public MediaPlayer mediaPlayer;
    /**
     * 记录播放的位置
     */
    int playPosition = 0;

    /**
     * 通知
     */
    private static Notification notification;
    /**
     * 通知栏视图
     */
    private static RemoteViews remoteViews;
    /**
     * 通知ID
     */
    private int NOTIFICATION_ID = 1;
    /**
     * 通知管理器
     */
    private static NotificationManager manager;
    /**
     * 音乐广播接收器
     */
    private MusicReceiver musicReceiver;

    /**
     * 通知栏控制Activity页面UI
     */
    private LiveDataBus.BusMutableLiveData<String> activityLiveData;

    /**
     * Activity控制通知栏UI
     */
    private LiveDataBus.BusMutableLiveData<String> notificationLiveData;


    private MediaPlayerHelp mMediaPlayerHelp;
    private MusicModel mMusicModel;
    private List<MusicModel> musicList;
    private int listPosition;

    private boolean isPrepared = true;

    public MusicService() {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    public class MusicBind extends Binder{
        /**
         * 1、设置音乐的方法(MusicModel)
         */
        public void setMusic(MusicModel musicModel){
            mMusicModel = musicModel;
            startForeground();//通知栏
        }
        public MusicService getService() {
            return MusicService.this;
        }
        /**
         * 2、播放音乐
         */
        /**
         * 播放音乐
         */
        public void playMusic(){
            /**
             * 1、判断当前音乐是否是已经正在播放的音乐
             * 2、如果当前音乐是已经正在播放的音乐的话，那么就直接执行start方法
             * 3、如果当前播放的音乐不是需要播放的音乐的话，那么就直接调用setPath方法
             */
            if(TextUtils.isEmpty(mMusicModel.getPath())) return ;

            if(mMediaPlayerHelp.getPath() != null && mMediaPlayerHelp.getPath().equals(mMusicModel.getPath().trim())){
                mMediaPlayerHelp.start();
            }
            else{
                isPrepared = false;
                mMediaPlayerHelp.setPath(mMusicModel.getPath());
                //监听当前音乐是否准备完成了
                mMediaPlayerHelp.setOnMediaPlayerHelperListener(new MediaPlayerHelp.OnMediaPlayerHelperListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //如果已经完成了的话，就调用start方法
                        isPrepared = true;
                        mMediaPlayerHelp.start();

                    }

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSelf();
//                        if(listPosition + 1 < musicList.size()){
//                            listPosition += 1;
//                            mMediaPlayerHelp.setPath(musicList.get(listPosition).getPath());
//                            EventBus.getDefault().post(MessageWrap.getInstance(listPosition,"changeMusic",null));//EventBus发布消息,用来改变音乐
//                        }
//                        else stopSelf();
                    }
                });
            }
        }

        /**
         * 3、暂停播放
         */
        public void stopMusic(){
            mMediaPlayerHelp.pause();
        }
    }
    /**
     * 获取当前播放位置
     *
     * @return
     */
    public int getPlayPosition() {
        return playPosition;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mList = LitePal.findAll(Song.class);
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);//注册订阅!!! 注意别重复订阅，detroy的时候取消订阅


        initRemoteViews();
        activityObserver();
        registerMusicReceiver();
        activityLiveData = LiveDataBus.getInstance().with("activity_control", String.class);

        mMediaPlayerHelp = MediaPlayerHelp.getInstance(this);

    }
    /**
     * 系统默认不允许不可见的后台服务播放音乐，借助Notification 在通知栏中显示，成为可见Service
     */
    /**
     * 设置服务在前台可见
     */


    //兼容8.0以及之前版本
    private Notification.Builder getNotificationBuilder(String  channelId,String channelName,int importance){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.enableLights(false);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
//            channel.setLightColor(Color.RED);//指定闪烁时的灯光颜色
            channel.canShowBadge();//桌面laucnher消息角标
            channel.enableVibration(false);//是否允许震动
//            channel.getAudioAttributes();//获取系统通知响铃声音的配置
            channel.getGroup();//获取通知渠道组
            channel.setBypassDnd(false);//设置可以绕过请勿打扰模式
//            channel.setVibrationPattern(new long[]{100L, 100L, 200L});//震动的模式
//            channel.shouldShowLights();//是否会发出亮光

            getManager().createNotificationChannel(channel);
        }

            return new Notification.Builder(this)
                    .setChannelId(channelId);
        }
    //获取系统服务
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    private void startForeground(){
//        /**
//         * 通知栏点击跳转的Intent
//         */
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
//                new Intent(this, WelcomActivity.class),PendingIntent.FLAG_CANCEL_CURRENT);

        /**
         * Notification
         */
        String CHANNEL_ONE_ID = "play_control";
        String CHANNEL_ONE_NAME = "播放控制";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        Notification.Builder builder = getNotificationBuilder(CHANNEL_ONE_ID,CHANNEL_ONE_NAME,importance);

        //点击整个通知时发送广播
        Intent intent = new Intent(getApplicationContext(), NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1 = new Intent(this,WelcomActivity.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this,-1,
                intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notifcation_view_id,pendingIntent1);

        builder.setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon_1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_1))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE; //设置为默认的声音

//        builder.setCustomBigContentView(remoteViews);
        getManager().notify(NOTIFICATION_ID,notification);

//        createNotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, importance);
//        Notification.Builder builder = new Notification.Builder(this)
//                .setContentTitle(mMusicModel.getName())
//                .setContentText(mMusicModel.getAuthor())
//                .setSmallIcon(R.mipmap.icon_1)
//                .setContentIntent(pendingIntent);

//        //初始化通知
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ONE_ID)
//                .setContentIntent(pendingIntent)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.icon_1)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_1))
//                .setCustomContentView(remoteViews)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setAutoCancel(false)
//                .setOnlyAlertOnce(true)
//                .setOngoing(true);
//        /**
//         * 设置 notification 在前台展示
//         */
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            //修改安卓8.1以上系统报错
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,                    NotificationManager.IMPORTANCE_MIN);
//            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
//            notificationChannel.setShowBadge(false);//是否显示角标
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(notificationChannel);
//            builder.setChannelId(CHANNEL_ONE_ID);
//        }


//        Notification notification = builder.build(); // 获取构建好的Notification
//        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(2, notification);

    }

    /**
     * 创建通知渠道
     *
     * @param channelId   渠道id
     * @param channelName 渠道名称
     * @param importance  渠道重要性
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        manager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    /**
     * 注册动态广播
     */
    private void registerMusicReceiver() {
        musicReceiver = new MusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY);
        intentFilter.addAction(PREV);
        intentFilter.addAction(NEXT);
        intentFilter.addAction(CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }
    /**
     * 初始化自定义通知栏 的按钮点击事件
     */
    private void initRemoteViews() {
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

        //通知栏控制器上一首按钮广播操作
        Intent intentPrev = new Intent(PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, intentPrev, 0);
        //为prev控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_previous, prevPendingIntent);

        //通知栏控制器播放暂停按钮广播操作  //用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        //为play控件注册事件c
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_play, playPendingIntent);

        //通知栏控制器下一首按钮广播操作
        Intent intentNext = new Intent(NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, intentNext, 0);
        //为next控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_next, nextPendingIntent);

        //通知栏控制器关闭按钮广播操作
        Intent intentClose = new Intent(CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, intentClose, 0);
        //为close控件注册事件
        remoteViews.setOnClickPendingIntent(R.id.btn_notification_close, closePendingIntent);

    }
    /**
     * 广播接收器 （内部类）
     */
    public class MusicReceiver extends BroadcastReceiver {

        public static final String TAG = "MusicReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            SPUtils.putBoolean(Constant.IS_CHANGE,true,context);
            //UI控制
            UIControl(intent.getAction(), TAG);
        }
    }

    /**
     * Activity的观察者
     */
    private void activityObserver() {
        notificationLiveData = LiveDataBus.getInstance().with("notification_control", String.class);
        notificationLiveData.observe(MusicService.this, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                //UI控制
                UIControl(state, TAG);
            }
        });
    }


    /**
     * 页面的UI 控制 ，通过服务来控制页面和通知栏的UI
     *
     * @param state 状态码
     * @param tag
     */
    private void UIControl(String state, String tag) {
        switch (state) {
            case PLAY:
                //暂停或继续
//                pauseOrContinueMusic();
                String msg = "";
                if(mMediaPlayerHelp.getPlayState())
                    msg = "pause";
                else
                    msg = "play";
                EventBus.getDefault().post(MessageWrap.
                        getInstance(-1,"UIControl",msg));//EventBus发布消息,用来返回
                break;
            case PREV:
                EventBus.getDefault().post(MessageWrap.
                        getInstance(-1,"UIControl","prev"));//EventBus发布消息,用来返回
//                previousMusic();
                BLog.d(tag, PREV);
                break;
            case NEXT:
                EventBus.getDefault().post(MessageWrap.
                        getInstance(-1,"UIControl","next"));//EventBus发布消息,用来返回
//                nextMusic();
                BLog.d(tag, NEXT);
                break;
            case CLOSE:
                EventBus.getDefault().post(MessageWrap.
                        getInstance(-1,"UIControl","close"));//EventBus发布消息,用来返回
//                closeNotification();
                BLog.d(tag, CLOSE);
                break;
            default:
                break;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageWrap event){
        if(event.getMessage().equals("musicList")){
            listPosition = event.getInteger();
//            Log.d(TAG,"位置 " + listPosition);
            musicList = (List<MusicModel>) event.getObject();
            MusicModel model = musicList.get(listPosition);
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);

            remoteViews.setTextViewText(R.id.tv_notification_song_name,model.getName());
            remoteViews.setTextViewText(R.id.tv_notification_singer,model.getAuthor());

            NotificationTarget notificationTarget = new NotificationTarget(
                    this,
                    R.id.iv_album_cover,
                    remoteViews,
                    notification,
                    NOTIFICATION_ID);

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(model.getPoster())
                    .into(notificationTarget);

            manager.notify(NOTIFICATION_ID, notification);
        }
        else if(event.getMessage().equals("PlayMusic")){
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);
            mMediaPlayerHelp.start();

            //发送通知
            manager.notify(NOTIFICATION_ID, notification);
        }
        else if(event.getMessage().equals("PauseMusic")){
            remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.play_black);
            mMediaPlayerHelp.pause();

            //发送通知
            manager.notify(NOTIFICATION_ID, notification);
        }
        else if(event.getMessage().equals("PrevMusic")){
            if(listPosition - 1 >= 0){
                listPosition -= 1;
                MusicModel model = musicList.get(listPosition);
                mMediaPlayerHelp.setPath(model.getPath());

                remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);

                remoteViews.setTextViewText(R.id.tv_notification_song_name,model.getName());
                remoteViews.setTextViewText(R.id.tv_notification_singer,model.getAuthor());


                NotificationTarget notificationTarget = new NotificationTarget(
                        this,
                        R.id.iv_album_cover,
                        remoteViews,
                        notification,
                        NOTIFICATION_ID);

                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(model.getPoster())
                        .into(notificationTarget);

                //发送通知
                manager.notify(NOTIFICATION_ID, notification);

                EventBus.getDefault().post(MessageWrap.getInstance(listPosition,"changeMusic",null));//EventBus发布消息,用来改变音乐

            }
        }
        else if(event.getMessage().equals("NextMusic")){
            if(listPosition + 1 < musicList.size()){
                listPosition += 1;

                MusicModel model = musicList.get(listPosition);
                mMediaPlayerHelp.setPath(model.getPath());

                remoteViews.setImageViewResource(R.id.btn_notification_play, R.drawable.pause_black);

                remoteViews.setTextViewText(R.id.tv_notification_song_name,model.getName());
                remoteViews.setTextViewText(R.id.tv_notification_singer,model.getAuthor());


                NotificationTarget notificationTarget = new NotificationTarget(
                        this,
                        R.id.iv_album_cover,
                        remoteViews,
                        notification,
                        NOTIFICATION_ID);

                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(model.getPoster())
                        .into(notificationTarget);
                //发送通知
                manager.notify(NOTIFICATION_ID, notification);

                EventBus.getDefault().post(MessageWrap.getInstance(listPosition,"changeMusic",null));//EventBus发布消息,用来改变音乐
            }
        }
        else if(event.getMessage().equals("RequrieMusicProgress")){
            int progress = 0;
            int total = 2000;
            if(isPrepared){
                progress = mMediaPlayerHelp.getProcess();
                total = mMediaPlayerHelp.getDura();
            }

            EventBus.getDefault().post(MessageWrap.
                    getInstance(progress,"AnswerMusicProgress",total));//EventBus发布消息,用来返回当前播放时间
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消订阅
        if (musicReceiver != null) {
            //解除动态注册的广播
            unregisterReceiver(musicReceiver);
        }

    }
}
