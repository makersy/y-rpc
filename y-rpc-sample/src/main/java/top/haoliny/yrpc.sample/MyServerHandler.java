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

public class MyServerHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //获取客户端发送过来的消息
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println("收到客户端" + ctx.channel().remoteAddress() + "发送的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //发送消息给客户端
    ctx.writeAndFlush(Unpooled.copiedBuffer("服务端收完消息，并给你发送一个问号?\n", CharsetUtil.UTF_8));
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    super.channelUnregistered(ctx);

    System.out.println("客户端" + ctx.channel().remoteAddress() + "退出了");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //发生异常，关闭通道
    cause.printStackTrace();
    ctx.close();
  }
}
