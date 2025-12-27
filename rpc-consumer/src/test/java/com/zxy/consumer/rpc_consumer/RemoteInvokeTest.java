package com.zxy.consumer.rpc_consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.annotation.*;
import com.zxy.netty_rpc.model.User;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.service.UserRemote;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("com.zxy")
public class RemoteInvokeTest {
	
	@RemoteInvoke
	public UserRemote userremote;
	
	
	@Test
	public void testSaveUser(){
		
//		for(int i=0;i<100;i++){
			User user = new User();
			user.setId(100);
			user.setName("张三");
			Response response=userremote.saveUser(user);
			System.out.println(JSONObject.toJSONString(response));
			System.out.println("成功了");
//		}
		

	}
	
}
	
	