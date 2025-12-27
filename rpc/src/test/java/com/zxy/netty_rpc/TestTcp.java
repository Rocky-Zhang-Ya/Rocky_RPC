package com.zxy.netty_rpc;

import org.junit.jupiter.api.Test;

import com.zxy.netty_rpc.bean.User;
import com.zxy.netty_rpc.clent.TcpClient;
import com.zxy.netty_rpc.param.ClientRequest;
import com.zxy.netty_rpc.param.Response;

public class TestTcp {
	
//	@Test
//	public void testGetResponse() {
//		
//		ClientRequest request = new ClientRequest();
//		request.setContent("test tcp 长连接 请求");
//		Response response=TcpClient.send(request);
//		System.out.println(response.getContent());
//	}

	@Test
	public void testSaveUser(){
		
			ClientRequest request = new ClientRequest();
			User user = new User();
			user.setId(123);
			user.setName("李华");
			// 客户端 可以从 zookeeper中 发现服务接口
			request.setCommand("com.zxy.netty_rpc.controller.UserController.saveUser");
			request.setContent(user);
			Response response = TcpClient.send(request);
			System.out.println("成功发送,并接收到响应："+response.getContent());

		
//		Response response = NettyClient.send(clientRequest);
//		System.out.println(response.getResult());
	}
	
	
//	@Test
//	public void testSaveUserList(){
//		
//
//			User user = new User();
//			user.setId(100);
//			user.setName("张三");
////			userremote.saveUser(user);
//			System.out.println("成功了");
//
//	}
	
	
}
