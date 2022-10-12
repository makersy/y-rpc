package top.haoliny.yrpc.client.inject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import top.haoliny.yrpc.client.proxy.ProxyFactory;
import top.haoliny.yrpc.common.annotation.RpcReference;

import java.lang.reflect.Field;

/**
 * @author yhl
 * @date 2022/10/8
 * @description 识别 {@code @RpcReference} 注解的字段，生成对应的代理对象并注入
 */

@Component
@Slf4j
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization (Object bean, String beanName) throws BeansException {
    Class<?> clz = bean.getClass();
    log.info("RpcReferenceBeanPostProcessor class: {}", clz.getName());
    for (Field field : clz.getDeclaredFields()) {
      RpcReference rpcReference = AnnotationUtils.getAnnotation(field, RpcReference.class);
      if (rpcReference == null) {
        continue;
      }
      log.info("RpcReferenceBeanPostProcessor find RpcReference, class: {}, field: {}, field type: {}", clz.getName(), field.getName(), field.getType());
      field.setAccessible(true);
      try {
        RpcReferenceFactoryBean<?> factoryBean = new RpcReferenceFactoryBean<>(field.getType());
        Object proxyBean = ProxyFactory.getProxyInstance(field.getType());
//        Object proxyBean = factoryBean.getObject();
        ReflectionUtils.setField(field, bean, proxyBean);
      } catch (Exception e) {
        log.error("error occurred when inject reference bean", e);
      }
    }
    return bean;
  }
}
