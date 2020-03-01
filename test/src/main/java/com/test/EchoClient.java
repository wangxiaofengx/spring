package com.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient() {
        this(0);
    }

    public EchoClient(int port) {
        this("localhost", port);
    }

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    Channel channel;

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // 注册线程池
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .option(ChannelOption.TCP_NODELAY, true)
                    .remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("正在连接中...");
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
                            ch.pipeline().addLast(new EchoClientHandler());
                            ch.pipeline().addLast(new ByteArrayEncoder());
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            channel = ch;
                        }
                    });
            // System.out.println("服务端连接成功..");

            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
            System.out.println("服务端连接成功..."); // 连接完成
//            b.connect().addListener(new ChannelFutureListener() {
//
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//
//                    if (channelFuture.isSuccess()) {
//                        channel = channelFuture.channel();
//                        System.out.println("连接成功");
//                    } else {
//                        System.out.println("每隔2s重连....");
//                        channelFuture.channel().eventLoop().schedule(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
////                                doConnect();
//                            }
//                        }, 2, TimeUnit.SECONDS);
//                    }
//                }
//            });

            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            System.out.println("连接已关闭.."); // 关闭完成

        } finally {
            //group.shutdownGracefully().sync(); // 释放线程池资源
        }
    }

    /**
     * 向服务端发送消息
     */
    private void sendData() {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < 1000; i++) {
            if (channel != null && channel.isActive()) {
                //获取一个键盘扫描器
                String nextLine = sc.nextLine();
                channel.writeAndFlush(nextLine);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        EchoClient client = new EchoClient("127.0.0.1", 8888);
        client.start(); // 连接127.0.0.1/65535，并启动
        client.sendData();
    }
}
