package io.github.makersy.yrpc.client.proxy;

import lombok.extern.slf4j.Slf4j;
import io.github.makersy.yrpc.client.util.ProxyUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author makersy
 * @date 2022/10/10
 * @description
 */

@Slf4j
public class ProxyFactory {

  private static ConcurrentHashMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> T getProxyInstance(Class<T> clz) {
    synchronized (clz) {
      if (!proxyCache.containsKey(clz)) {
        try {
          T instance = ProxyUtil.newInstance(clz);
          proxyCache.put(clz, instance);
        } catch (Exception e) {
          log.error("ProxyFactory create proxy error", e);
          return null;
        }
        log.info("new proxy instance success, class: {}", clz);
      }
    }
    return (T) proxyCache.get(clz);
  }
}
