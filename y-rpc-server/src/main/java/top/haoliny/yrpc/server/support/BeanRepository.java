package top.haoliny.yrpc.server.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@Component
public class BeanRepository implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  public <T> T getBean(Class<T> clz) {
    return applicationContext.getBean(clz);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
