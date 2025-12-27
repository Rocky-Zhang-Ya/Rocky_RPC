package com.zxy.netty_rpc.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.zxy.netty_rpc.annotation.RemoteInvoke;
import com.zxy.netty_rpc.clent.NettyClient;
import com.zxy.netty_rpc.client.TcpClient;
import com.zxy.netty_rpc.param.ClientRequest;
import com.zxy.netty_rpc.param.Response;

@Component
public class InvokeProxy implements BeanPostProcessor {
	public static Enhancer enhancer = new Enhancer();  // 创建动态代理对象
	
	public Object postProcessAfterInitialization(Object bean, String arg1) throws BeansException {
		return bean;
	}
	
	//对属性的所有方法和属性类型放入到HashMap中
	private void putMethodClass(HashMap<Method, Class> methodmap, Field field) {
		Method[] methods = field.getType().getDeclaredMethods();
		for(Method method : methods){
			methodmap.put(method, field.getType());
		}
		
	}

	
	public Object postProcessBeforeInitialization(Object bean, String arg1) throws BeansException {
//		System.out.println(bean.getClass().getName());
		Field[] fields = bean.getClass().getDeclaredFields();
		for(Field field : fields){
			if(field.isAnnotationPresent(RemoteInvoke.class)){ // 扫描field字段上是否有 remoteinvoke的注解
				//  类的字段通常是 private（私有）的, 在类外部也可以访问 通过将该字段的可访问标志设置为 true
				field.setAccessible(true); 
				
				final HashMap<Method, Class> methodmap = new HashMap<Method, Class>();
				putMethodClass(methodmap,field);
				Enhancer enhancer = new Enhancer(); // 创建动态代理对象
				
				enhancer.setInterfaces(new Class[]{field.getType()}); // 设置动态代理对象的目标接口
				enhancer.setCallback(new MethodInterceptor() {
					@Override
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
						ClientRequest clientRequest = new ClientRequest();
						// args 是被拦截的方法的参数 数组
						clientRequest.setContent(args[0]);
						String command= methodmap.get(method).getName()+"."+method.getName();
//						String command = method.getName();//修改
						System.out.println("InvokeProxy中的Command是:"+command);
						clientRequest.setCommand(command);
						
						Response response = TcpClient.send(clientRequest);
						return response;
					}
				});
				try {
					// 在执行这行代码之前，bean 中的那个字段（例如 userService）可能是 null
					// 依赖注入:  把 bean 这个对象实例中，对应 field 字段的值，强制替换为 enhancer.create() 生成的代理对象
					field.set(bean, enhancer.create());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return bean;
	}
}
