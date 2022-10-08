package top.haoliny.yrpc.client.inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.client.proxy.ProxyFactory;
import top.haoliny.yrpc.common.annotation.RpcReference;

import java.lang.reflect.Field;

/**
 * @author yhl
 * @date 2022/10/8
 * @description 识别 {@code @RpcReference} 注解的字段，生成对应的代理对象并注入
 */

@Component
@Order()
@Slf4j
@RequiredArgsConstructor
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

  private final ProxyFactory proxyFactory;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    Class<?> clz = bean.getClass();
    do {
      for (Field field : clz.getDeclaredFields()) {
        RpcReference rpcReference = field.getAnnotation(RpcReference.class);
        if (rpcReference == null) {
          continue;
        }
        field.setAccessible(true);
        try {
          field.set(bean, proxyFactory.getOrCreateInstance(clz));
        } catch (IllegalAccessException e) {
          log.error("error creating rpc reference proxy bean", e);
        }
      }
      clz = clz.getSuperclass();
    } while (clz != null && clz.getSuperclass() != null);
    return bean;
  }
}
