package io.github.makersy.yrpc.client.util;

import io.github.makersy.yrpc.client.proxy.ClientInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * @author makersy
 * @date 2022/10/8
 * @description
 */

public class ProxyUtil {

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clz) {
    return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new ClientInvocationHandler<>(clz));
  }
}
