package top.haoliny.yrpc.server.handler;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.util.SpringUtil;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

  @Resource(name = "serverThreadPool")
  private ExecutorService serverThreadPool;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof RpcRequest) {
      RpcRequest request = (RpcRequest) msg;
      log.debug("netty server received request: {}", request);

      // 线程
      serverThreadPool.submit(new RpcRunnable(request, ctx));
    }
  }

  @RequiredArgsConstructor
  class RpcRunnable implements Runnable {
    private final RpcRequest request;
    private final ChannelHandlerContext ctx;

    @Override
    public void run() {
      RpcResponse response = new RpcResponse();
      response.setRequestId(request.getRequestId());

      try {
        Object invokeResult = invoke(request);
        response.setResult(invokeResult);
      } catch (Throwable e) {
        response.setThrowable(e);
        log.error("RpcServerHandler invoke err", e);
      }
      ctx.writeAndFlush(response);
    }
  }

  /**
   * 执行本地service方法
   *
   * @param request client请求
   * @return 执行结果
   * @throws Throwable 可能出现的异常
   */
  private Object invoke(RpcRequest request) throws Throwable {
    //todo 避免使用forName，后续可优化。后续可在启动时加载所有rpc service，将servicename和service bean的映射缓存起来
    Class<?> serviceClass = Class.forName(request.getClassName());

    Object serviceBean = SpringUtil.getBean(serviceClass);
    Preconditions.checkNotNull(serviceBean, String.format("failed to find service bean, className: %s", request.getClassName()));

    Method method = serviceClass.getMethod(request.getMethodName(), request.getParameterTypes());
    Preconditions.checkNotNull(method, String.format("failed to find service method, methodName: %s", request.getMethodName()));

    return method.invoke(serviceBean, request.getParameters());
  }

}
