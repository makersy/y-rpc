package io.github.makersy.yrpc.common.util;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

public class CommonUtil {

  /**
   * 根据channel获取服务器地址
   *
   * @param channel channel
   * @return ip:port
   */
  public static String getRemoteAddr(Channel channel) {
    InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
    return socketAddress.getHostString() + ":" + socketAddress.getPort();
  }

  public static InetSocketAddress convertInetSocketAddress(String addrAndPort) {
    String[] split = addrAndPort.split(":");
    return InetSocketAddress.createUnresolved(split[0], Integer.parseInt(split[1]));
  }
}
