package io.github.makersy.yrpc.client.proxy;

import io.github.makersy.yrpc.client.RpcClient;
import io.github.makersy.yrpc.common.model.Result;
import io.github.makersy.yrpc.common.model.RpcRequest;
import io.github.makersy.yrpc.common.model.RpcResponse;
import io.github.makersy.yrpc.common.util.SpringUtil;
import io.github.makersy.yrpc.config.ProtocolConfig;
import io.github.makersy.yrpc.config.RegistryConfig;
import io.github.makersy.yrpc.registry.Registry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author makersy
 * @date 2022/9/24
 * @description
 */

@Slf4j
public class ClientInvocationHandler<T> implements InvocationHandler {

  private final Class<T> clz;

  private ReentrantLock lock;
  private RpcClient rpcClient;

  public ClientInvocationHandler(Class<T> clz) {
    this.clz = clz;
    this.lock = new ReentrantLock();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class<?> methodClass = method.getDeclaringClass();
    String methodName = method.getName();
    Class<?>[] parameterTypes = method.getParameterTypes();

    if (rpcClient == null) {
      initRpcClient();
    }

    RpcRequest request = new RpcRequest();
    request.setClassName(methodClass.getName());
    request.setMethodName(methodName);
    request.setParameterTypes(parameterTypes);
    request.setParameters(args);

    try {
      RpcResponse rpcResponse = rpcClient.send(request);
      log.debug("Get rpcResponse: {}", rpcResponse);

      if (rpcResponse.getThrowable() != null) {
        return Result.catchException(rpcResponse.getThrowable());
      }

      return rpcResponse.getResult();
    } catch (Exception e) {
      log.error("ClientInvocationHandler send request failed", e);
      return RpcResponse.buildErrorResponse(request, e);
    }
  }

  private void initRpcClient() {
    lock.lock();
    try {
      if (rpcClient == null) {
        rpcClient = new RpcClient(
                SpringUtil.getBean(Registry.class),
                SpringUtil.getBean(RegistryConfig.class),
                SpringUtil.getBean(ProtocolConfig.class));
        log.debug("init rpc client success, class: {}", clz);
      }
    } finally {
      lock.unlock();
    }
  }
}
