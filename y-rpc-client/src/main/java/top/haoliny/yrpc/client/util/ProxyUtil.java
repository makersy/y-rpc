package top.haoliny.yrpc.client.util;

import top.haoliny.yrpc.client.proxy.ClientInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * @author yhl
 * @date 2022/10/8
 * @description
 */

public class ProxyUtil {

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clz) {
    return (T) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new ClientInvocationHandler<>(clz));
  }
}
