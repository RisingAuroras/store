package com.example.cloudmusic.utils;

public class MessageWrap {

    public Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public final String message;

    public static MessageWrap getInstance(String message,Object obj) {
        return new MessageWrap(message,obj);
    }

    private MessageWrap(String message,Object obj) {
        this.message = message;
        this.object = obj;
    }

    public String getMessage(){
        return message;
    }
}