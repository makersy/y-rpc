package io.github.makersy.yrpc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * @author makersy
 * @date 2022/9/20
 * @description
 */

@Component
public class SpringUtil implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  public static <T> T getBean(Class<T> clz) {
    return applicationContext.getBean(clz);
  }

  @Override
  public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
    SpringUtil.applicationContext = applicationContext;
  }
}
