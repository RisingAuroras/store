package com.example.cloudmusic.models;

import io.realm.RealmObject;

public class MusicModel extends RealmObject implements Cloneable{
    /**
     * {
     *           "musicId": "101",
     *           "name": "Nostalgic Piano",
     *           "poster": "http://res.lgdsunday.club/poster-1.png",
     *           "path": "http://res.lgdsunday.club/Nostalgic%20Piano.mp3",
     *           "author": "Rafael Krux"
     *         }
     */

    private String musicId;
    private String name;
    private String poster;
    private String path;
    private String author;
    /**
     * 歌曲长度
     */
    public int duration;
    /**
     * 歌曲的大小
     */
    public long size;
    /**
     * 专辑ID
     */
    public int albumId;
    /**
     * 专辑名
     */
    public String album;
    /**
     * 当前歌曲选中
     */
    public boolean isCheck;

    public boolean isLocal;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Object clone() throws CloneNotSupportedException {
        return (MusicModel)super.clone();
    }
}
