package io.github.makersy.yrpc.server.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.github.makersy.yrpc.common.codec.RpcDecoder;
import io.github.makersy.yrpc.common.codec.RpcEncoder;
import io.github.makersy.yrpc.common.model.RpcRequest;
import io.github.makersy.yrpc.common.model.RpcResponse;
import io.github.makersy.yrpc.common.serialize.SerializationFactory;
import io.github.makersy.yrpc.config.ProtocolConfig;
import io.github.makersy.yrpc.config.RpcServerConfig;
import io.github.makersy.yrpc.registry.Registry0;
import io.github.makersy.yrpc.server.handler.RpcServerHandler;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class RpcServer {

  private final RpcServerHandler rpcServerHandler;
  private final RpcServerConfig serverConfig;
  private final ProtocolConfig protocolConfig;
  private final Registry0 registry;

  private ServerBootstrap bootstrap;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private Channel serverChannel;

  public void start() throws InterruptedException {
    // 启动netty server
    bootstrap = new ServerBootstrap();
    bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServer"));
    workerGroup = new NioEventLoopGroup();

    bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            // 禁用Nagle算法
            .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
            // 端口复用
            .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
            // 设置缓冲区分配器
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(new ChannelInitializer<>() {
              @Override
              protected void initChannel(@Nonnull Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4))
                        .addLast("yrpc-encoder", new RpcEncoder(SerializationFactory.get(protocolConfig.getSerialization()), RpcResponse.class))
                        .addLast("yrpc-decoder", new RpcDecoder(SerializationFactory.get(protocolConfig.getSerialization()), RpcRequest.class))
                        .addLast("yrpc-server-handler", rpcServerHandler);
              }
            });

    serverChannel = bootstrap.bind(serverConfig.getPort()).sync().channel().closeFuture().channel();
    log.debug("netty succeed to bind port {}", serverConfig.getPort());

    // 注册
    try {
      registry.registerProvider();
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  @PreDestroy
  public void stop() {
    if (serverChannel != null) {
      serverChannel.close();
      if (serverChannel.parent() != null) {
        serverChannel.parent().close();
      }
    }
  }
}
