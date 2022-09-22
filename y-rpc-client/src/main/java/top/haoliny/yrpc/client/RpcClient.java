package top.haoliny.yrpc.client;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.haoliny.yrpc.client.cache.ChannelCache;
import top.haoliny.yrpc.client.handler.RpcClientHandler;
import top.haoliny.yrpc.common.codec.RpcDecoder;
import top.haoliny.yrpc.common.codec.RpcEncoder;
import top.haoliny.yrpc.common.config.ProtocolConfig;
import top.haoliny.yrpc.common.exception.ExceptionCode;
import top.haoliny.yrpc.common.exception.YrpcException;
import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;
import top.haoliny.yrpc.common.serialize.Serialization;
import top.haoliny.yrpc.common.serialize.SerializationFactory;

import javax.annotation.PostConstruct;

import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author yhl
 * @date 2022/9/23
 * @description
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class RpcClient {
  private final ProtocolConfig protocolConfig;
  private final RpcClientHandler rpcClientHandler;

  private static final int CLIENT_CONNNECT_TIMEOUT = 3000;
  private static final ReentrantLock lock = new ReentrantLock();

  private Bootstrap bootstrap;
  private Serialization serialization;

  @PostConstruct
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
                        .addLast("yrpc-client-handler", rpcClientHandler);
              }
            });
  }

  private Channel getChannel(String addr) throws Exception {
    Channel channel = ChannelCache.get(addr);
    if (channel != null) {
      return channel;
    }

    String[] split = addr.split(":");
    // 建立连接
    ChannelFuture future = bootstrap.connect(split[0], Integer.parseInt(split[1]));
    // 阻塞等待结果
    boolean ret = future.awaitUninterruptibly(CLIENT_CONNNECT_TIMEOUT, MILLISECONDS);
    if (ret && future.isSuccess()) {
      // 此时如果channel池有已建立的连接了，就舍弃掉这次刚建立的channel
      lock.lock();

      channel = ChannelCache.get(addr);
      if (channel != null) {
        future.channel().close();
      } else {
        channel = future.channel();
      }

      lock.unlock();
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
