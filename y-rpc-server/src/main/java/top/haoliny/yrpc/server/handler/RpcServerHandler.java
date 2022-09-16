package top.haoliny.yrpc.server.handler;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof RpcRequest) {
      RpcRequest request = (RpcRequest) msg;
      RpcResponse response = new RpcResponse();
      response.setRequestId(request.getRequestId());

      try {
        Object invokeResult = invoke(request);
        response.setResult(invokeResult);
      } catch (Throwable e) {
        log.error("RpcServerHandler catch error", e);
        response.setThrowable(e);
      }
      ctx.writeAndFlush(response);
    }

  }

  /**
   * @param request client请求
   * @return 执行结果
   * @throws Throwable 可能出现的异常
   */
  private Object invoke(RpcRequest request) throws Throwable {
    //todo 避免使用forName，后续可优化。后续可在启动时加载所有rpc service，将servicename和service bean的映射缓存起来
    Class<?> serviceClass = Class.forName(request.getClassName());

    Object serviceBean = applicationContext.getBean(serviceClass);
    Preconditions.checkNotNull(serviceBean, String.format("failed to find service bean, className: %s", request.getClassName()));

    Method method = serviceClass.getMethod(request.getMethodName(), request.getParameterTypes());
    Preconditions.checkNotNull(method, String.format("failed to find service method, methodName: %s", request.getMethodName()));

    return method.invoke(serviceBean, request.getParameters());
  }

  @Override
  public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
