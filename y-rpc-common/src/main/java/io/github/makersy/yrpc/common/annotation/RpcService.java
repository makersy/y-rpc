package io.github.makersy.yrpc.common.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author makersy
 * @date 2022/10/8
 * @description 暴露provider的服务
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
    Class<?> interfaceClass() default void.class;
}
