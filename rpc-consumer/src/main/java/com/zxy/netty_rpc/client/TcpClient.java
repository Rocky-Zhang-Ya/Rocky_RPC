package com.zxy.netty_rpc.client;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.client.ResultFuture;
import com.zxy.netty_rpc.constant.Constants;
import com.zxy.netty_rpc.handler.SimpleClientHandler;
import com.zxy.netty_rpc.param.ClientRequest;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.zk.ServerWatcher;
import com.zxy.netty_rpc.zk.ZookeeperFactory;

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
	
	public static final Bootstrap b = new Bootstrap(); // Netty客户端的辅助启动类
	public static  ChannelFuture f = null;
	
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
            
            
            CuratorFramework client = ZookeeperFactory.create();
            try {
            	// getChildren是为 获取操作子节点的 入口
            	List<String> serverPath = client.getChildren().forPath(Constants.SERVER_PATH);
            	// 需要去重 因为 serverPath 子节点list中 可能有多个相同的 子节点路径
//            	Set<String> realServerPath= new HashSet<String>();
            	
            	//客户端加上ZK监听器 监听服务器的变化 SERVER_PATH下的子路径 注册的远程服务是否变化
				CuratorWatcher watcher = new ServerWatcher();
				client.getChildren().usingWatcher(watcher ).forPath(Constants.SERVER_PATH);
            	// 普通轮询
				for(String path :serverPath){
					String[] str = path.split("#");
					ChannelManager.realServerPath.add(str[0]+"#"+str[1]);
					ChannelFuture channnelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
					ChannelManager.addChnannel(channnelFuture);
				}
				
//				// **加权轮询的负载均衡
//				for(String path :serverPath){
//					String[] str = path.split("#");
//					int weight = Integer.valueOf(str[2]);
//					if (weight>0) {
//						for (int w = 0; w <= weight; w++) {
//							ChannelManager.realServerPath.add(str[0]+"#"+str[1]);
//							ChannelFuture channnelFuture = TcpClient.b.connect(str[0], Integer.valueOf(str[1]));
//							ChannelManager.addChnannel(channnelFuture);
//						}
//					}
//					
//				}
				
				if(ChannelManager.realServerPath.size()>0){
					String[] netMessageArray = ChannelManager.realServerPath.toArray()[0].toString().split("#");
					host = netMessageArray[0];
					port = Integer.valueOf(netMessageArray[1]);
				}
            	
            	
//				client.getChildren().forPath(Constants.SERVER_PATH);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            
            
            // Start the client.
  
//				f = b.connect(host, port).sync();    client
		
	}
	
	// 每一个请求都是一个 单独的连接(长连接) , 不同request的响应 会有并发问题
	// 发送数据
	public static Response send(ClientRequest request) {
		f=ChannelManager.get(ChannelManager.position);
		f.channel().writeAndFlush(JSONObject.toJSONString(request));
		f.channel().writeAndFlush("\r\n");
		
		Long timeout = 200l;
		ResultFuture rf = new ResultFuture(request);
		
		return rf.get(timeout);
	}

}
