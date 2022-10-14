package io.github.makersy.yrpc.client.handler;

import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.github.makersy.yrpc.client.cache.ChannelCache;
import io.github.makersy.yrpc.client.support.RpcFuture;
import io.github.makersy.yrpc.common.exception.ExceptionCode;
import io.github.makersy.yrpc.common.exception.YrpcException;
import io.github.makersy.yrpc.common.model.RpcResponse;
import io.github.makersy.yrpc.common.util.CommonUtil;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@ChannelHandler.Sharable
@Component
@RequiredArgsConstructor
@Slf4j
public class RpcClientHandler extends ChannelDuplexHandler {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ChannelCache.add(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ChannelCache.remove(CommonUtil.getRemoteAddr(ctx.channel()));
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);
    if (msg instanceof RpcResponse) {
      RpcResponse resp = (RpcResponse) msg;
      log.debug("client received server response: {}", resp);

      // 从缓存池中找到rpcFuture
      RpcFuture rpcFuture = RpcFuture.findById(resp.getRequestId());

      if (rpcFuture == null) {
        throw new YrpcException(ExceptionCode.CLIENT_INTERNAL_ERROR,
                String.format("Can't find rpcFuture, requestId: %s", resp.getRequestId()));
      }

      if (!rpcFuture.isDone()) {
        rpcFuture.complete(resp);
      }
    }
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    super.write(ctx, msg, promise);
    promise.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        log.debug("netty client send msg success, msg: {}", msg);
      }
    });
  }
}
