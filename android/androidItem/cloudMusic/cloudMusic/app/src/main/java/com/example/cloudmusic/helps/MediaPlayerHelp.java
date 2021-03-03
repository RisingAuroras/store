package com.example.cloudmusic.helps;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.Song;
import com.example.cloudmusic.utils.MessageWrap;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1、直接在Activity中去创建播放音乐，音乐与Activity绑定，音乐与Activity绑定运行时播放音乐，退出时停止播放
 * 2、通过全局单例类与Application绑定，当Application运行时播放，被杀死时停止播放
 * 3、通过Service 进行音乐播放,当Service运行时播放，被杀死时停止播放
 */
public class MediaPlayerHelp  {
    private static MediaPlayerHelp instance;
    /**
     * 记录播放的位置
     */
    int playPosition = 0;
    /**
     * 歌曲间隔时间
     */
    private static final int INTERNAL_TIME = 1000;

    private Context mContext;
    private String mPath;
    /**
     * 音乐播放器
     */
    private MediaPlayer mMediaPlayer;
    private OnMediaPlayerHelperListener onMediaPlayerHelperListener;

    public void setOnMediaPlayerHelperListener(OnMediaPlayerHelperListener onMediaPlayerHelperListener) {
        this.onMediaPlayerHelperListener = onMediaPlayerHelperListener;
    }

    public static MediaPlayerHelp getInstance(Context context){//单例器
        if(instance == null) {
            synchronized (MediaPlayerHelp.class){//同步
                if(instance == null){//如果同步依然为空
                    instance = new MediaPlayerHelp(context);
                }
            }
        }
        return instance;
    }

    public MediaPlayerHelp(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    public boolean getPlayState(){
        return  mMediaPlayer.isPlaying();
    }
    /**
     * 1、setPath:当前需要播放的音乐
     * 2、start:播放音乐
     * 3、pause:暂停播放
     */
    /**
     * setPath:当前需要播放的音乐
     * @param path
     */

    public void setPath(String path){
        /**
         * 1、音乐正在播放，重置音乐播放状态
         * 2、设置音乐播放路径
         * 3、准备播放
         */

        /**
         * 》错误逻辑《 ----暂停切换歌曲程序退出
         * 当音乐进行切换的时候，如果音乐处于播放状态，那么就重置音乐的播放状态
         * 而如果 音乐没有处于播放状态的话（暂停）,就不去重置播放状态
         *
         * 解决方法，增加判断，当前是否为切换音乐的状态，
         */
//        1、音乐正在播放，/或者切换音乐 重置音乐播放状态
        if(mMediaPlayer.isPlaying() || !path.equals(mPath)){
            mMediaPlayer.reset();
        }

        mPath = path;
//        2、设置音乐播放路径
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        3、准备播放
        mMediaPlayer.prepareAsync();//异步加载
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if(onMediaPlayerHelperListener != null){
                    onMediaPlayerHelperListener.onPrepared(mp);
                }
            }
        });
//        4、监听音乐播放完成
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(onMediaPlayerHelperListener != null){
                    onMediaPlayerHelperListener.onCompletion(mp);
                }
            }
        });
    }

    /**
     * start:播放音乐
     */
    public void start(){
        if(mMediaPlayer.isPlaying()) return;
        mMediaPlayer.start();
    }

    /**
     * pause:暂停播放
     */
    public void pause(){
        mMediaPlayer.pause();
    }

    /**
     * 返回正在播放的音乐路径
     * @return
     */
    public String getPath(){
        return mPath;
    }
    public interface OnMediaPlayerHelperListener{
        void onPrepared(MediaPlayer mp);
        void onCompletion(MediaPlayer mp);
    }

    public int getProcess(){
        return mMediaPlayer.getCurrentPosition();
    }
    public int getDura(){
        return mMediaPlayer.getDuration();
    }
}
