package top.haoliny.yrpc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@Component
public class BeanRepository implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  public static <T> T getBean(Class<T> clz) {
    return applicationContext.getBean(clz);
  }

  @Override
  public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
    BeanRepository.applicationContext = applicationContext;
  }
}
