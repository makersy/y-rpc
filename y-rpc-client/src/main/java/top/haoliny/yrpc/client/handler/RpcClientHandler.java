package top.haoliny.yrpc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.haoliny.yrpc.common.model.RpcResponse;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {

  }
}
