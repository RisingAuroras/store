package com.example.cloudmusic.helps;

import android.content.Context;

import com.example.cloudmusic.migration.Migration;
import com.example.cloudmusic.models.AlbumModel;
import com.example.cloudmusic.models.MusicModel;
import com.example.cloudmusic.models.MusicSourceModel;
import com.example.cloudmusic.models.Song;
import com.example.cloudmusic.models.UserModel;
import com.example.cloudmusic.utils.DataUtils;

import java.io.FileNotFoundException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmHelper {

    private Realm mRealm;

    public RealmHelper() {
        mRealm = Realm.getDefaultInstance();//引用计数管理内存，每次调用后加1
    }

    /**
     * Realm数据库发生结构性变化（模型或者模型中的字段出现了新增，修改，删除）的时候，我们就需要对数据库进行迁移
     */

    /**
     * 告诉Realm数据库需要迁移，并且为Realm设置最新的配置
     */
    public static void migration(){
        RealmConfiguration conf = getRealmConf();

        //Realm最新配置
        Realm.setDefaultConfiguration(conf);
        //告诉Realm需要数据迁移
        try {
            Realm.migrateRealm(conf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 返回 RealmConfiguration
     * @return
     */
    private static RealmConfiguration getRealmConf(){
        return new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new Migration())
                .build();
    }
    /**
     * 关闭数据库
     */
    public void close(){
        if(mRealm != null && !mRealm.isClosed()){
            mRealm.close();
        }
    }
    /**
     * 保存用户信息
     */
    public void saveUser(UserModel userModel){
        mRealm.beginTransaction();
        mRealm.insert(userModel);//放入到缓存之中,若主键存在则报错，须自己处理
//        mRealm.insertOrUpdate(userModel);//若主键已经存在，则更新
        mRealm.commitTransaction();//提交事务，保存到数据库中
    }

    /**
     * 返回所有用户
     */
    public List<UserModel> getAllUser(){
        RealmQuery<UserModel> query = mRealm.where(UserModel.class);
        RealmResults<UserModel> results = query.findAll();

        return results;
    }

    /**
     * 验证用户信息
     */
    public boolean validateUser(String phone,String password){
        boolean res = false;
        RealmQuery<UserModel> query = mRealm.where(UserModel.class);
        query = query.equalTo("phone", phone).equalTo("password", password);
        UserModel userModel = query.findFirst();

        if(userModel != null){
            res =  true;
        }
        return res;
    }
    /**
     * 获取当前用户
     */
    public UserModel getUser(){
        RealmQuery<UserModel> query = mRealm.where(UserModel.class);
        UserModel userModel = query.equalTo("phone", UserHelper.getInstance().getPhone())
                .findFirst();
        return userModel;
    }

    /**
     * 修改密码
     */
    public void changePassword(String password){
        UserModel userModel = getUser();
        mRealm.beginTransaction();
        userModel.setPassword(password);
        mRealm.commitTransaction();
    }

    /**
     * 1、用户登录，存放数据
     * 2、用户退出，删除数据
     */
    /**
     * 保存音乐源数据
     */
    public void setMusicSource(Context context){
        //拿到资源文件中的数据
        String musicSourceJson = DataUtils.getJsonFromAssets(context, "DataSource.json");
        mRealm.beginTransaction();
        mRealm.createObjectFromJson(MusicSourceModel.class,musicSourceJson);
        mRealm.commitTransaction();
    }
    /**
     * 删除音乐源数据
     * 1、RealmResult delete
     * 2、Realm delete 删除这个模型下的所有数据
     */
    public void removeMusicSource(){
        mRealm.beginTransaction();
        mRealm.delete(MusicSourceModel.class);
        mRealm.delete(MusicModel.class);
        mRealm.delete(AlbumModel.class);
        mRealm.commitTransaction();
    }
    /**
     * 返回音乐源数据
     */
    public MusicSourceModel getMusicSource(){
        return mRealm.where(MusicSourceModel.class).findFirst();
    }
    /**
     * 返回歌单
     */
    public AlbumModel getAlbum(String albumId){
        return mRealm.where(AlbumModel.class).equalTo("albumId",albumId).findFirst();
    }
    /**
     * 返回音乐
     */
    public MusicModel getMusic(String musicId){
        return mRealm.where(MusicModel.class).equalTo("musicId",musicId).findFirst();
    }
    /**
     * 修改歌曲信息
     */
    public void changeMusicModel(MusicModel model){
        MusicModel musicModel = getMusic(model.getMusicId());
        mRealm.beginTransaction();
        musicModel.setCheck(model.isCheck);
        musicModel.setAlbum(model.getAlbum());
        musicModel.setAlbumId(model.getAlbumId());
        musicModel.setAuthor(model.getAuthor());
        musicModel.setDuration(model.getDuration());
        musicModel.setName(model.getName());
        musicModel.setMusicId(model.getMusicId());
        musicModel.setPath(model.getPath());
        musicModel.setPoster(model.getPoster());
        musicModel.setSize(model.getSize());

        mRealm.commitTransaction();
    }
    /**
     * 修改歌曲信息 Check
     */
    public void changeMusicModelCheck(String musicId){
        MusicModel musicModel = getMusic(musicId);
        boolean ok = musicModel.isCheck();
        mRealm.beginTransaction();
        musicModel.setCheck(!ok);
        mRealm.commitTransaction();
    }
    public void changeMusicModelCheckIsTrue(String musicId){
        MusicModel musicModel = getMusic(musicId);
        boolean ok = true;
        mRealm.beginTransaction();
        musicModel.setCheck(ok);
        mRealm.commitTransaction();
    }
    public void changeMusicModelCheckIsFalse(String musicId){
        MusicModel musicModel = getMusic(musicId);
        boolean ok = false;
        mRealm.beginTransaction();
        musicModel.setCheck(ok);
        mRealm.commitTransaction();
    }

    public void addMusicModel(Integer id,Song song){
        mRealm.beginTransaction();
        MusicModel model = mRealm.createObject(MusicModel.class);
        model.setMusicId(id.toString());
        model.setName(song.getSong());
        model.setAuthor(song.getSinger());
        model.setPoster(song.getAlbum());
        model.setPath(song.getPath());
        model.setDuration(song.getDuration());
        model.setSize(song.getSize());
        model.setAlbumId(song.getAlbumId());
        model.setAlbum( song.getAlbum());
        model.setCheck(song.isCheck());
        mRealm.commitTransaction();
    }

    public void addMusicModels(List<Song> songs){
        mRealm.beginTransaction();
        int len = songs.size();
        int baseId = 1000;
        for (int i = 0;i < len;++ i){
            Song song = songs.get(i);
            Integer id = baseId + i;
            MusicModel model = mRealm.createObject(MusicModel.class);
            model.setMusicId(id.toString());
            model.setName(song.getSong());
            model.setAuthor(song.getSinger());
            model.setPoster("https://image.lnstzy.cn/aoaodcom/2019-08/17/201908170750199553.jpg.h700.jpg");
            model.setPath(song.getPath());
            model.setDuration(song.getDuration());
            model.setSize(song.getSize());
            model.setAlbumId(song.getAlbumId());
            model.setAlbum( song.getAlbum());
            model.setCheck(song.isCheck());
            model.setLocal(true);
        }
        mRealm.commitTransaction();

    }
    public List<MusicModel> getMusicList(){
        return mRealm.where(MusicModel.class).findAll();
    }
    public List<MusicModel> getLocalMusic(){
        return mRealm.where(MusicModel.class).equalTo("isLocal",true).findAll();
    }
}
