package com.example.cloudmusic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activitys.WelcomActivity;
import com.example.cloudmusic.helps.MediaPlayerHelp;
import com.example.cloudmusic.helps.RealmHelper;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.utils.DataUtils;
import com.example.cloudmusic.utils.MessageWrap;

import org.greenrobot.eventbus.EventBus;

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
public class MusicService extends Service {

    public static final int NOTIFICATION_ID = 1;//值不可为0

    /**
     * 通知管理器
     */
    private static NotificationManager manager;
    /**
     * 通知
     */
    private static Notification notification;

    /**
     * 通知栏视图
     */
    private static RemoteViews remoteViews;

    private MediaPlayerHelp mMediaPlayerHelp;
    private MusicModel mMusicModel;
    public MusicService() {
    }

    public class MusicBind extends Binder{
        /**
         * 1、设置音乐的方法(MusicModel)
         */
        public void setMusic(MusicModel musicModel){
            mMusicModel = musicModel;
            startForeground();//通知栏
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
                mMediaPlayerHelp.setPath(mMusicModel.getPath());
                //监听当前音乐是否准备完成了
                mMediaPlayerHelp.setOnMediaPlayerHelperListener(new MediaPlayerHelp.OnMediaPlayerHelperListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //如果已经完成了的话，就调用start方法
                        mMediaPlayerHelp.start();
                    }

                    @Override
                    public void onCompletion(MediaPlayer mp) {
//                        stopSelf();
                        Integer integer = new Integer(mMusicModel.getMusicId());
                        integer = integer + 1;
                        String nextId = integer.toString();
                        RealmHelper realmHelper = new RealmHelper();
                        MusicModel nextMusicModel = realmHelper.getMusic(nextId);
                        if(nextMusicModel == null) {
                            stopSelf();
                        }
                        else{
                            Integer pos = DataUtils.getPos() + 1;
                            EventBus.getDefault().post(MessageWrap.getInstance(pos.toString(),null));//EventBus发布消息,用来展示播放状态
                            setMusic(nextMusicModel);
                            playMusic();
                        }
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


    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRemoteViews();
        mMediaPlayerHelp = MediaPlayerHelp.getInstance(this);
    }
    /**
     * 系统默认不允许不可见的后台服务播放音乐，借助Notification 在通知栏中显示，成为可见Service
     */
    /**
     * 设置服务在前台可见
     */
    private void startForeground(){
        /**
         * 通知栏点击跳转的Intent
         */
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                new Intent(this, WelcomActivity.class),PendingIntent.FLAG_CANCEL_CURRENT);
        /**
         * Notification
         */
        String CHANNEL_ONE_ID = "com.primedu.cn";
        String CHANNEL_ONE_NAME = "Channel One";
//        Notification.Builder builder = new Notification.Builder(this)
//                .setContentTitle(mMusicModel.getName())
//                .setContentText(mMusicModel.getAuthor())
//                .setSmallIcon(R.mipmap.icon_1)
//                .setContentIntent(pendingIntent);

        //初始化通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "play_control")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon_1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_1))
                .setCustomContentView(remoteViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true);
        /**
         * 设置 notification 在前台展示
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }


        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        startForeground(NOTIFICATION_ID,notification);

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
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, intentPlay, 0);
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

}
