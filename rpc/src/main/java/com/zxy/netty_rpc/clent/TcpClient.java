package com.zxy.netty_rpc.clent;

import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.future.ResultFuture;
import com.zxy.netty_rpc.handler.SimpleClientHandler;
import com.zxy.netty_rpc.param.ClientRequest;
import com.zxy.netty_rpc.param.Response;

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

public class TcpClient {
	
	static final Bootstrap b = new Bootstrap(); // Netty客户端的辅助启动类
	static  ChannelFuture f = null;
	
	static {
			String host = "localhost";
	        int port = 8080;
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
 
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
            try {
				f = b.connect(host, port).sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}
	
	// 每一个请求都是一个 单独的连接(长连接) , 不同request的响应 会有并发问题
	// 发送数据
	public static Response send(ClientRequest request) {
		f.channel().writeAndFlush(JSONObject.toJSONString(request));
		f.channel().writeAndFlush("\r\n");
		ResultFuture rf = new ResultFuture(request);
		
		return rf.get();
	}

}
