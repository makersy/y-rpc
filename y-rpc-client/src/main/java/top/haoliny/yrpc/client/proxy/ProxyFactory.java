package top.haoliny.yrpc.client.proxy;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.client.util.ProxyUtil;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

@Component
public class ProxyFactory implements ApplicationContextAware {

  private static Map<Class<?>, Object> cache = new LinkedHashMap<>();

  private ApplicationContext applicationContext;

  @SuppressWarnings("unchecked")
  public <T> T getOrCreateInstance(Class<T> clz) {
    if (!cache.containsKey(clz)) {
      // fixme 注入到spring bean容器，而不是作为静态缓存
      cache.put(clz, ProxyUtil.newInstance(clz));
    }
    return (T) cache.get(clz);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
