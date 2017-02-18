package com.hurl.demo.netty.client;

import com.hurl.demo.netty.core.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by hurongliang on 2017/2/17.
 */
public class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringEncoder(Constants.charset));
                            socketChannel.pipeline().addLast(new StringDecoder(Constants.charset));
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture f = b.connect("127.0.0.1", Constants.serverPort).sync();
            if (f.isSuccess()) {
                LOG.info("connected to " + Constants.serverPort);
            }
            f.channel().closeFuture().sync();
            LOG.info("disconnected.");
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<String>{

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("channel acvited to " + getAddr(ctx));
            ctx.writeAndFlush(String.valueOf(1));
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
            LOG.info("收到服务器端消息 : " + s);
            channelHandlerContext.writeAndFlush(String.valueOf(Integer.parseInt(s) +1));
        }
    }
}
