package com.zxy.netty_rpc.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
	public static CuratorFramework client;
	
	public static CuratorFramework create() {
		if (client == null) {
			// 参数1: baseSleepTimeMs (初始休眠时间，比如 1000ms)
			// 参数2: maxRetries (最大重试次数，比如 3次)
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			//	创建并启动 Zookeeper 客户端	
			client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
			client.start();
			
		}
		
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client = create();
		client.create().forPath("/netty");
		
	}
	

}
