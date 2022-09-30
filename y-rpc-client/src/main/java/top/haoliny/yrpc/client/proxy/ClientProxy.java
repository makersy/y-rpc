package top.haoliny.yrpc.client.proxy;

import java.lang.reflect.Proxy;

/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

public class ClientProxy {

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clz) {
    return (T) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new ClientInvocationHandler<>(clz));
  }
}
