package top.haoliny.yrpc.client.handler;

import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.client.cache.ChannelCache;
import top.haoliny.yrpc.client.support.RpcFuture;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.util.CommonUtil;

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
      RpcFuture rpcFuture = RpcFuture.findById(resp.getRequestId());
      if (rpcFuture != null && !rpcFuture.isDone()) {
        rpcFuture.complete(resp);
      }
    }
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    super.write(ctx, msg, promise);

    //todo 发送完请求之后的回调
    promise.addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (msg instanceof RpcRequest) {
          RpcRequest request = (RpcRequest) msg;
          
        }
      }
    });
  }
}
