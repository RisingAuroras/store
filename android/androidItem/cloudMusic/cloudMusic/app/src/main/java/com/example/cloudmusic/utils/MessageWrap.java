package com.example.cloudmusic.utils;

public class MessageWrap {

    public int integer;
    public Object object;
    public final String message;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public static MessageWrap getInstance(int pos, String message, Object obj) {
        return new MessageWrap(pos,message,obj);
    }

    private MessageWrap(int integer,String message,Object obj) {
        this.integer = integer;
        this.message = message;
        this.object = obj;
    }

    public String getMessage(){
        return message;
    }
}