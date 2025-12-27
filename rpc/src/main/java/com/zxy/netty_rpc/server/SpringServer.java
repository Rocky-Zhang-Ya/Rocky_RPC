package com.zxy.netty_rpc.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("com.zxy")
public class SpringServer {
	
	public static void main(String[] args) {
//		ApplicationContext context = new AnnotationConfigApplicationContext(SpringServer.class);
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("server"); // 激活 server 模式
		context.register(SpringServer.class);
		context.refresh();
	}

}
