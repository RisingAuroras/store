package com.example.cloudmusic.utils;

public class StaticProgressMemo {
    public static Integer pos[] = {-1};//记录最后的位置
    public static boolean isRunning[] = {false};//记录服务是否在运行
    public static String musicIds[] = {null};//记录musicId

    public static String getMusicId(){
        return musicIds[0];
    }
    public static void setMusicId(String musicId){
        musicIds[0] = musicId;
    }
    public static void setPos(Integer[] pos) {
        StaticProgressMemo.pos = pos;
    }

    public static Integer getPos(){
        return pos[0];
    }
    public static void setPos(Integer i){
        pos[0] = i;
    }

    public static boolean getIsRunning() {
        return isRunning[0];
    }

    public static void setIsRunning(boolean isRunning) {
        StaticProgressMemo.isRunning[0] = isRunning;
    }
}
