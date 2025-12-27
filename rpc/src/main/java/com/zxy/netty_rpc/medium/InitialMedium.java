package com.zxy.netty_rpc.medium;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.zxy.netty_rpc.annotation.Remote;

//  中介者模式  使用 动态代理 来实现通用代码和业务代码的 解耦

@Component
public class InitialMedium implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		
		if (bean.getClass().isAnnotationPresent(Remote.class)) { // 判断 类上面 bean 是否有controller的注解
			Method [] methods=bean.getClass().getDeclaredMethods();
			for (Method m: methods) {
				String key = bean.getClass().getInterfaces()[0].getName()+"."+m.getName();
				HashMap<String, BeanMethod> map =Media.interferceMap;
				BeanMethod beanMethod =new BeanMethod();
				beanMethod.setBean(bean);
				beanMethod.setMethod(m);
				map.put(key, beanMethod);
				System.out.println(key);
			}
		
		}
		return bean;

	}
	

}
