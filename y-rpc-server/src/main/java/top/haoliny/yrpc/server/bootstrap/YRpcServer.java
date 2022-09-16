package top.haoliny.yrpc.server.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.common.codec.RpcDecoder;
import top.haoliny.yrpc.common.codec.RpcEncoder;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.serialize.JsonSerialization;
import top.haoliny.yrpc.server.handler.RpcServerHandler;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

@Slf4j
@RequiredArgsConstructor
public class YRpcServer {

  private final RpcServerHandler rpcServerHandler;
  private final ServerBootstrap bootstrap;

  public void start() {
    bootstrap.childHandler(new ChannelInitializer<>() {
      @Override
      protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4))
                .addLast(new RpcEncoder(new JsonSerialization(), RpcResponse.class))
                .addLast(new RpcDecoder(new JsonSerialization(), RpcRequest.class))
                .addLast(rpcServerHandler);
      }
    });
  }
}
