package top.haoliny.yrpc.client.support;

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
import top.haoliny.yrpc.client.cache.ChannelCache;
import top.haoliny.yrpc.client.handler.RpcClientHandler;
import top.haoliny.yrpc.common.codec.RpcDecoder;
import top.haoliny.yrpc.common.codec.RpcEncoder;
import top.haoliny.yrpc.common.constants.Constants;
import top.haoliny.yrpc.common.exception.ExceptionCode;
import top.haoliny.yrpc.common.exception.YrpcException;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.model.URL;
import top.haoliny.yrpc.common.serialize.SerializationFactory;
import top.haoliny.yrpc.common.util.CommonUtil;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author yhl
 * @date 2022/9/28
 * @description
 */

@Slf4j
public class NettyClient {

  private static final int CLIENT_CONNECT_TIMEOUT = 3000;

  private final URL url;

  private Bootstrap bootstrap;

  private volatile Channel channel;

  public NettyClient(URL url) {
    this.url = url;
    init();
    try {
      connect();
    } catch (YrpcException e) {
      log.error("channel connect error", e);
    }
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
                        .addLast("yrpc-encoder", new RpcEncoder(SerializationFactory.get(url.getParameter(Constants.SERIALIZATION_KEY)), RpcRequest.class))
                        .addLast("yrpc-decoder", new RpcDecoder(SerializationFactory.get(url.getParameter(Constants.SERIALIZATION_KEY)), RpcResponse.class))
                        .addLast("yrpc-client-handler", new RpcClientHandler());
              }
            });
  }

  public void connect() throws YrpcException {
    // 建立连接
    ChannelFuture future = bootstrap.connect(url.getHost(), url.getPort());

    // 阻塞等待结果
    boolean completed = future.awaitUninterruptibly(CLIENT_CONNECT_TIMEOUT, MILLISECONDS);
    if (completed && future.isSuccess()) {
      // 如果channel池有已建立的连接，就舍弃掉这次新建立的channel，使用已有channel
      channel = ChannelCache.putIfAbsent(CommonUtil.getRemoteAddr(future.channel()), future.channel());
      if (channel == null) {
        channel = future.channel();
      } else {
        future.channel().close();
      }
    } else if (future.cause() != null) {
      // 连接异常
      throw new YrpcException(ExceptionCode.CLIENT_CONNECT_FAILED, future.cause());
    } else {
      // 连接发生未知异常
      throw new YrpcException(ExceptionCode.CLIENT_CONNECT_FAILED, "unknown error");
    }
    this.channel = channel;
  }

  public void send(Object msg) {
    if (channel == null || !channel.isActive()) {
      log.error("send message failed since channel is not active");
    }
    channel.writeAndFlush(msg);
  }
}
