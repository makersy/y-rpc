package top.haoliny.yrpc.client.proxy;

import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.client.RpcClient;
import top.haoliny.yrpc.common.config.ProtocolConfig;
import top.haoliny.yrpc.common.config.RegistryConfig;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.registry.Registry0;
import top.haoliny.yrpc.common.util.SpringUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yhl
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
    Class<?> className = method.getDeclaringClass();
    String methodName = method.getName();
    Class<?>[] parameterTypes = method.getParameterTypes();

    if (rpcClient == null) {
      initRpcClient();
    }
    RpcResponse rpcResponse = rpcClient.send(className.getName(), methodName, parameterTypes, args);
    log.debug("Get rpcResponse: {}", rpcResponse);

    return rpcResponse.getResult();
  }

  private void initRpcClient() {
    lock.lock();
    try {
      if (rpcClient == null) {
        rpcClient = new RpcClient(
                SpringUtil.getBean(Registry0.class),
                SpringUtil.getBean(RegistryConfig.class),
                SpringUtil.getBean(ProtocolConfig.class));
      }
    } finally {
      lock.unlock();
    }
  }
}
