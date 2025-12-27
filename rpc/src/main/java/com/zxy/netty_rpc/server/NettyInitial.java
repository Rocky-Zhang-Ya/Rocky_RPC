package com.zxy.netty_rpc.server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.zxy.netty_rpc.constant.Constants;
import com.zxy.netty_rpc.factory.ZookeeperFactory;
import com.zxy.netty_rpc.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;


@Profile("server")
@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent>{
	
	
	public void start() throws Exception {
		
		
			EventLoopGroup bossgroup = new NioEventLoopGroup();
			EventLoopGroup workergroup = new NioEventLoopGroup();
		try {	
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossgroup,workergroup);
			bootstrap.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, false)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
			    @Override
			    public void initChannel(SocketChannel ch) throws Exception {
			    	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
			    	ch.pipeline().addLast(new StringDecoder());
			    	// 设置读 写空闲的时间阈值 心跳检测  然后再handler中 实现通知心跳信息的逻辑
			    	ch.pipeline().addLast(new IdleStateHandler(60, 15, 10, TimeUnit.SECONDS));
			    	ch.pipeline().addLast(new StringEncoder());
			    	ch.pipeline().addLast(new ServerHandler());
			    	
			    }
			});
			int port =8080;
			int weight =2; // 加权轮询的负载均衡
			ChannelFuture f= bootstrap.bind(port).sync();
			CuratorFramework client = ZookeeperFactory.create();
			InetAddress inetAddress = InetAddress.getLocalHost();

			//	普通轮询方式	 使用zookeeper 注册 服务节点 
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
					.forPath(Constants.SERVER_PATH +"/"+ inetAddress.getHostAddress()+"#"+port+"#");
			
//			// **加权轮询的负载均衡  注册方法
//			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
//			.forPath(Constants.SERVER_PATH +"/"+ inetAddress.getHostAddress()+"#"+port+"#"+weight+"#");
			
			
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
			bossgroup.shutdownGracefully();
			workergroup.shutdownGracefully();
			
		}
		
		
			
			
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		try {
			this.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
