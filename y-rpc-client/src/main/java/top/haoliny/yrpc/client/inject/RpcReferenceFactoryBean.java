package top.haoliny.yrpc.client.inject;

import org.springframework.beans.factory.FactoryBean;
import top.haoliny.yrpc.client.proxy.ProxyFactory;

/**
 * @author yhl
 * @date 2022/10/11
 * @description
 */

public class RpcReferenceFactoryBean<T> implements FactoryBean<T> {
  private final Class<T> clz;

  public RpcReferenceFactoryBean(Class<T> clz) {
    this.clz = clz;
  }

  @Override
  public T getObject() throws Exception {
    return ProxyFactory.getProxyInstance(clz);
  }

  @Override
  public Class<?> getObjectType() {
    return null;
  }
}
