package com.zxy.netty_rpc.client;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelFuture;

public class ChannelManager {
	
	public static CopyOnWriteArrayList<ChannelFuture>  channelFutures = new CopyOnWriteArrayList<ChannelFuture>();
	public static  CopyOnWriteArrayList<String> realServerPath=new CopyOnWriteArrayList<String>();
	public static AtomicInteger  position = new AtomicInteger(0);//先采用轮询的方式使用send

	public static void removeChnannel(ChannelFuture channel){
		channelFutures.remove(channel);
	}
	
	public static void addChnannel(ChannelFuture channel){
		channelFutures.add(channel);
	}
	public static void clearChnannel(){
		channelFutures.clear();
	}
	
	// 使用负载均衡算法 获取服务   同时需要用原子类 保证多个client 获取连接 也是安全的
	public static ChannelFuture get(AtomicInteger i) {
		
		//目前采用轮循机制
		ChannelFuture channelFuture = null;
		int size = channelFutures.size();
		if(i.get()>=size){
			channelFuture = channelFutures.get(0);
			ChannelManager.position= new AtomicInteger(1);
		}else{
			channelFuture = channelFutures.get(i.getAndIncrement());
		}
		// 如果上面 获取的channelFuture 的channel 不活跃，重新调用get
//		if (!channelFuture.channel().isActive()) {
//			removeChnannel(channelFuture);
//			return get(position);
//		}
		
		
		return channelFuture;
	}
	

}
