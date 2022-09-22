package top.haoliny.yrpc.client.proxy;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

@RequiredArgsConstructor
public class ClientInvocationHandler<T> implements InvocationHandler {

  private final Class<T> clz;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


    return null;
  }
}
