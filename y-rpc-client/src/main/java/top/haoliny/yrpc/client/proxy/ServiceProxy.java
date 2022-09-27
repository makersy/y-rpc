package top.haoliny.yrpc.client.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

public class ServiceProxy {

  private static final Map<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> T getOrCreateProxy(Class<T> clz) {
    if (proxyCache.containsKey(clz)) {
      return (T) proxyCache.get(clz);
    } else {
      T p = newInstance(clz);
      proxyCache.put(clz, p);
      return p;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> clz) {
    return (T) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new ClientInvocationHandler<>(clz));
  }
}
