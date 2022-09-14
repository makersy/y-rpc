package top.haoliny.yrpc.sample;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * @author yhl
 * @date 2022/9/13
 * @description
 */

public class MyClient {

  public static void main(String[] args) throws Exception {
    NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
    try {
      //创建bootstrap对象，配置参数
      Bootstrap bootstrap = new Bootstrap();
      //设置线程组
      bootstrap.group(eventExecutors)
              //设置客户端的通道实现类型
              .channel(NioSocketChannel.class)
              //使用匿名内部类初始化通道
              .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                  //添加客户端通道的处理器
                  socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(65536, Unpooled.copiedBuffer("\n".getBytes())));
                  socketChannel.pipeline().addLast(new MyClientHandler());
                }
              });
      System.out.println("客户端准备就绪");
      //连接服务端
      ChannelFuture future = bootstrap.connect("127.0.0.1", 6666);
      future.get();
      //发一条消息
      future.channel()
              .writeAndFlush(Unpooled.copiedBuffer(new Date() + " hello server\n", CharsetUtil.UTF_8))
              .await();

      future.channel()
              .writeAndFlush(Unpooled.copiedBuffer(new Date() + " how are you\n", CharsetUtil.UTF_8))
              .await();

      //对通道关闭进行监听
      future.channel().closeFuture().sync();
    } finally {
      //关闭线程组
      eventExecutors.shutdownGracefully();
    }
  }
}
