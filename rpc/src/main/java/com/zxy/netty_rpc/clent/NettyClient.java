package com.zxy.netty_rpc.clent;

import java.util.concurrent.TimeUnit;

import com.zxy.netty_rpc.handler.SimpleClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

public class NettyClient {
	
	public static void main(String[] args) throws Exception {
		String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            Bootstrap b = new Bootstrap(); // Netty客户端的辅助启动类
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                	ch.pipeline().addLast(new StringDecoder());
                	ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new SimpleClientHandler());
                }
            });
            
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            
            f.channel().writeAndFlush("hello server from client");
            f.channel().writeAndFlush("\r\n");
            // Wait until the connection is closed.
            //同步阻塞在这里，为了等待 clienthandler 接受服务端响应信息处理完成，调用了channel.close后停止阻塞
            f.channel().closeFuture().sync();
            //处理完接受 响应后就停止上面的阻塞，获取channel中AttributeKey的响应值。            
            Object resultObject = f.channel().attr(AttributeKey.valueOf("rsp")).get();
            System.out.println("获取到的服务端的响应数据："+ resultObject.toString());
            
        } finally {
            workerGroup.shutdownGracefully();
        }
	}

}
