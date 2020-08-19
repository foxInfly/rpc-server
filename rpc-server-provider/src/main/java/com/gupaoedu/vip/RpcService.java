package com.gupaoedu.vip;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解发布服务
 * @author : lipu
 * @since : 2020-08-19 22:43
 */
@Target(ElementType.TYPE)//对象范围
@Retention(RetentionPolicy.RUNTIME)
@Component //被spring去扫描
public @interface RpcService {

    Class<?> value();//拿到服务的接口

}
