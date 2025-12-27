package com.zxy.netty_rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ElementType.FIELD}) //标注 注解的对象  可以放在类和方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemoteInvoke {

	String value() default "";

}
