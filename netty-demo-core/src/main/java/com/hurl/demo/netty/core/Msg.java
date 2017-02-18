package com.hurl.demo.netty.core;

/**
 * Created by hurongliang on 2017/2/17.
 */
public class Msg {
    private MsgType type;
    private String data;

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
