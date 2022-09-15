package top.haoliny.yrpc.sample;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author yhl
 * @date 2022/9/13
 * @description
 */

public class MyClientHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //发送消息到服务端
    ctx.writeAndFlush(Unpooled.copiedBuffer("client channel active\n", CharsetUtil.UTF_8));
    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //接收服务端发送过来的消息
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println("收到服务端" + ctx.channel().remoteAddress() + "的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
    super.channelRead(ctx, msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //发生异常，关闭通道
    cause.printStackTrace();
    ctx.close();
  }
}
