package com.hurl.demo.netty.server;

import com.hurl.demo.netty.core.Constants;
import com.hurl.demo.netty.core.Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * Created by hurongliang on 2017/2/17.
 */
public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringEncoder(Constants.charset));
                            socketChannel.pipeline().addLast(new StringDecoder(Constants.charset));
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(Constants.serverPort).sync();
            if (f.isSuccess()) {
               LOG.info("listening at " + Constants.serverPort);
            }

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class ServerHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
            LOG.info("channel acvited to " + getAddr(ctx));
        }

        private String getAddr(ChannelHandlerContext ctx) {
            InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
            return addr.getAddress().getHostAddress() + ":" + addr.getPort();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("channel inactive to " + getAddr(ctx));
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            LOG.info("收到客户端" + Util.getAddr(channelHandlerContext) + "消息 : " + s);
            Thread.sleep(1000);
            int count = Integer.valueOf(s);
            channelHandlerContext.writeAndFlush(String.valueOf(count + 1));
        }
    }
}
