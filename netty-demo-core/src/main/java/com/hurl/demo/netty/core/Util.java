package com.hurl.demo.netty.core;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * Created by hurongliang on 2017/2/18.
 */
public class Util {
    public static String getAddr(ChannelHandlerContext channelHandlerContext) {
        InetSocketAddress addr = (InetSocketAddress)channelHandlerContext.channel().remoteAddress();
        String host = addr.getAddress().getHostAddress();
        int port = addr.getPort();
        return host + ":" + port;
    }
}
