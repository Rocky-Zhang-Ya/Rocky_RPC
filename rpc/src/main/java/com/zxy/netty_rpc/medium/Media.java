package com.zxy.netty_rpc.medium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;
import com.zxy.netty_rpc.param.Response;
import com.zxy.netty_rpc.param.ServerRequest;

public class Media {
	
	
	
	public static HashMap<String,BeanMethod> interferceMap= new HashMap<String,BeanMethod>();
		
	
	static {
		interferceMap =new HashMap<String,BeanMethod>();
		
		
	}
	
	
	private static Media m= null;
	private Media() {
		
	}

	// 设置一个单例模式
	public static Media newInstance() {

		if (m==null) {
			m=new Media();
		}
		return m;
	}
	
	// 反射处理  来获取请求中的数据，并向动态代理的接口传参和调用该接口 最终获取结果。
	public Response process(ServerRequest serverRequest) {
		Response result =null;
		try {
			String command = serverRequest.getCommand();
			BeanMethod beanMethod =interferceMap.get(command);
			if (beanMethod== null) {
				return null;
			}
			
			Object bean = beanMethod.getBean();
			Method m=beanMethod.getMethod();
			Class<?> paramType = m.getParameterTypes()[0];
			Object content = serverRequest.getContent();
			
			Object args = JSONObject.parseObject(JSONObject.toJSONString(content),paramType);
		
			result =(Response) m.invoke(bean, args);
			result.setId(serverRequest.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
}
