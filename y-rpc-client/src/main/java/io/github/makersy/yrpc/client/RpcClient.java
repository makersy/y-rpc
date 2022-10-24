package io.github.makersy.yrpc.client;

import io.github.makersy.yrpc.client.cache.ChannelCache;
import io.github.makersy.yrpc.client.handler.RpcClientHandler;
import io.github.makersy.yrpc.client.support.RpcFuture;
import io.github.makersy.yrpc.common.codec.RpcDecoder;
import io.github.makersy.yrpc.common.codec.RpcEncoder;
import io.github.makersy.yrpc.common.exception.ExceptionCode;
import io.github.makersy.yrpc.common.exception.YrpcException;
import io.github.makersy.yrpc.common.model.RpcRequest;
import io.github.makersy.yrpc.common.model.RpcResponse;
import io.github.makersy.yrpc.common.model.URL;
import io.github.makersy.yrpc.common.serialize.SerializationFactory;
import io.github.makersy.yrpc.common.util.CommonUtil;
import io.github.makersy.yrpc.common.util.SpringUtil;
import io.github.makersy.yrpc.config.ProtocolConfig;
import io.github.makersy.yrpc.config.RegistryConfig;
import io.github.makersy.yrpc.registry.Registry;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author makersy
 * @date 2022/9/23
 * @description
 */

@Slf4j
public class RpcClient {

  private final Registry registry;
  private final RegistryConfig registryConfig;
  private final ProtocolConfig protocolConfig;

  private static final int CLIENT_CONNECT_TIMEOUT = 3000;

  private Bootstrap bootstrap;

  public RpcClient() {
    this(SpringUtil.getBean(Registry.class),
            SpringUtil.getBean(RegistryConfig.class),
            SpringUtil.getBean(ProtocolConfig.class));
  }

  public RpcClient(Registry registry,
                   RegistryConfig registryConfig,
                   ProtocolConfig protocolConfig) {
    this.registry = registry;
    this.registryConfig = registryConfig;
    this.protocolConfig = protocolConfig;
    init();
  }

  public void init() {
    // 创建并配置客户端Bootstrap
    bootstrap = new Bootstrap();
    bootstrap
            // 设置线程池组
            .group(new NioEventLoopGroup(new DefaultThreadFactory("NettyClientExecutor")))
            // 设置连接存活探测
            .option(ChannelOption.SO_KEEPALIVE, true)
            // 禁用Nagle算法
            .option(ChannelOption.TCP_NODELAY, true)
            // 设置缓冲区分配器
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            // 设置连接超时
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            // 设置channel类型
            .channel(NioSocketChannel.class)
            // 设置channel handler
            .handler(new ChannelInitializer<>() {
              @Override
              protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4))
                        .addLast("yrpc-encoder", new RpcEncoder(SerializationFactory.get(protocolConfig.getSerialization()), RpcRequest.class))
                        .addLast("yrpc-decoder", new RpcDecoder(SerializationFactory.get(protocolConfig.getSerialization()), RpcResponse.class))
                        .addLast("yrpc-client-handler", SpringUtil.getBean(RpcClientHandler.class));
              }
            });
  }

  public RpcResponse send(RpcRequest request) throws Exception {
    List<URL> serviceProviders = registry.findServiceProviders(request.getClassName());
    if (CollectionUtils.isEmpty(serviceProviders)) {
      throw new YrpcException(ExceptionCode.NO_PROVIDERS, "no provider");
    }

    // 随机一个节点
    // todo 负载均衡
    URL serviceUrl = serviceProviders.get(ThreadLocalRandom.current().nextInt(serviceProviders.size()));

    // 写入channel缓冲区
    Channel channel = getChannel(serviceUrl.getRawAddress());
    channel.writeAndFlush(request).await();

    RpcFuture rpcFuture = new RpcFuture(request);

    return rpcFuture.get();
  }

  /**
   * 如果channel已建立，则返回该channel；否则建立新的channel并返回
   *
   * @param addr ip:port
   * @return started channel
   * @throws Exception if connect failed
   */
  private Channel getChannel(String addr) throws Exception {
    // 优先使用已建立channel
    Channel channel = ChannelCache.get(addr);
    if (channel != null) {
      return channel;
    }

    // 建立连接
    InetSocketAddress socketAddress = CommonUtil.convertInetSocketAddress(addr);
    ChannelFuture future = bootstrap.connect(socketAddress);

    // 阻塞等待结果
    boolean completed = future.awaitUninterruptibly(CLIENT_CONNECT_TIMEOUT, MILLISECONDS);
    if (completed && future.isSuccess()) {
      // 如果channel池有已建立的连接，就舍弃掉这次新建立的channel，使用已有channel
      Channel before = ChannelCache.get(addr);
      if (before == null || !before.isActive()) {
        channel = future.channel();
      } else {
        channel = before;
        future.channel().close();
      }
      log.info("netty connect to {} success!", CommonUtil.getRemoteAddr(channel));
    } else if (future.cause() != null) {
      // 连接异常
      throw new YrpcException(ExceptionCode.CLIENT_CONNECT_FAILED, future.cause());
    } else {
      // 连接发生未知异常
      throw new YrpcException(ExceptionCode.CLIENT_CONNECT_FAILED, "unknown error");
    }
    return channel;
  }

}
