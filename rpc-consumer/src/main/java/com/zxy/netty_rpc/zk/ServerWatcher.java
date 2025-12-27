package com.zxy.netty_rpc.zk;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;


import com.zxy.netty_rpc.client.ChannelManager;
import com.zxy.netty_rpc.client.TcpClient;

import io.netty.channel.ChannelFuture;

public class ServerWatcher implements CuratorWatcher {

	@Override
	public void process(WatchedEvent event) throws Exception {
		
		System.out.println("process------------------------");
		CuratorFramework client = ZookeeperFactory.create();
		String path = event.getPath();
		client.getChildren().usingWatcher(this).forPath(path);
		List<String> newServerPaths = client.getChildren().forPath(path);
		System.out.println(newServerPaths);
		ChannelManager.realServerPath.clear();  // 防止服务关闭后 重启 没有清空原来的数据
		
//		// 加权轮询 负载均衡版本
//		for(String p :newServerPaths){
//			String[] str = p.split("#");
//			int weight = Integer.valueOf(str[2]);
//			if (weight>0) {
//				for (int w = 0; w <= weight; w++) {
//					ChannelManager.realServerPath.add(str[0]+"#"+str[1]);//去重
//				}
//			}
//			
//		}
//		ChannelManager.clearChnannel();
//		for(String realServer:ChannelManager.realServerPath){  // 在去重后的serverPath中连接
//			String[] str = realServer.split("#");
//			int weight = Integer.valueOf(str[2]);
//			if (weight>0) {
//				for (int w = 0; w <= weight; w++) {
//					ChannelFuture channnelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
//					ChannelManager.addChnannel(channnelFuture);	
//				}
//			}
//				
//		}
		
		//普通 轮询
		for(String p :newServerPaths){
			String[] str = p.split("#");
			ChannelManager.realServerPath.add(str[0]+"#"+str[1]);//去重
		}
	
		ChannelManager.clearChnannel();
		for(String realServer:ChannelManager.realServerPath){  // 在去重后的serverPath中连接
			String[] str = realServer.split("#");
			ChannelFuture channnelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
			ChannelManager.addChnannel(channnelFuture);		
		}
	}
		

	}


