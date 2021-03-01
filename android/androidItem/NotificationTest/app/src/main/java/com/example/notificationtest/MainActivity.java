package com.example.notificationtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.notificationtest.R;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MainActivity extends AppCompatActivity {
    NotificationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomNotification();
    }
//
//
//    @TargetApi(Build.VERSION_CODES.O)
//    private void createNotificationChannel(String channelId, String channelName, int importance) {
//        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(
//                NOTIFICATION_SERVICE);
//        notificationManager.createNotificationChannel(channel);
//    }
//
//    public void sendChatMsg(View view) {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(this, "chat")
//                .setContentTitle("收到一条聊天消息")
//                .setContentText("今天中午吃什么？")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
//                .setAutoCancel(true)
//                .build();
//        manager.notify(1, notification);
//    }
//
//    public void sendSubscribeMsg(View view) {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(this, "subscribe")
//                .setContentTitle("收到一条订阅消息")
//                .setContentText("地铁沿线30万商铺抢购中！")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .setAutoCancel(true)
//                .build();
//        manager.notify(2, notification);
//    }

    //发送一个普通通知
    private void sendNormalNotification(){
        Notification.Builder builder = getNotificationBuilder();
        getManager().notify(1,builder.build());
    }
    //魔方一个带进度的通知
    private void sendProgressNotification(){
        final Notification.Builder builder = getNotificationBuilder();
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);

        getManager().notify(2,builder.build());

        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i = 0;i <= 100;++ i){
                    try {
                        Thread.sleep(100);
                        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
                        builder.setProgress(100,i,false);

                        getManager().notify(2,builder.build());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    //自定义状态栏
    private void setCustomNotification(){
        Notification.Builder builder = getNotificationBuilder();

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification);

        remoteViews.setTextViewText(R.id.tv_notification_song_name,"rapper");
        remoteViews.setTextViewText(R.id.tv_notification_singer,"jiangyuns");

        //PendingIntent 即将 要发生的意图，它是可以被 取消被更新的
        Intent intent = new Intent(this,MainActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,-1,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notifcation_view_id,pendingIntent);

        builder.setCustomBigContentView(remoteViews);

        getManager().notify(3,builder.build());
    }
    //获取系统服务
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    //兼容8.0以及之前版本
    private Notification.Builder getNotificationBuilder(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel_id","channel_name",NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可否绕过请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪烁时的灯光颜色
            channel.canShowBadge();//桌面laucnher消息角标
            channel.enableVibration(true);//是否允许震动
            channel.getAudioAttributes();//获取系统通知响铃声音的配置
            channel.getGroup();//获取通知渠道组
            channel.setBypassDnd(true);//设置可以绕过请勿打扰模式
            channel.setVibrationPattern(new long[]{100L,100L,200L});//震动的模式
            channel.shouldShowLights();//是否会发出亮光

            getManager().createNotificationChannel(channel);
        }

        return new Notification.Builder(this)
                .setAutoCancel(true)
                .setChannelId("channel_id")
                .setContentText("明天就要开学了啊")
                .setSmallIcon(R.mipmap.ic_launcher);
    }

//    删除通知
    private void disChannel(String channelId){
        getManager().cancel(Integer.parseInt(channelId));
    }
}