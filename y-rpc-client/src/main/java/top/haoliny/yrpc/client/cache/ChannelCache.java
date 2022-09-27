package top.haoliny.yrpc.client.cache;

import io.netty.channel.Channel;
import top.haoliny.yrpc.common.util.CommonUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhl
 * @date 2022/9/24
 * @description client已建立channel的缓存
 */

public class ChannelCache {
  private static final ConcurrentHashMap<String, Channel> channelCache = new ConcurrentHashMap<>();

  public static void put(String addr, Channel channel) {
    channelCache.put(addr, channel);
  }

  /**
   * 将已建立channel添加至缓存
   *
   * @param channel 已建立的channel
   */
  public static void add(Channel channel) {
    channelCache.put(CommonUtil.getRemoteAddr(channel), channel);
  }

  /**
   * 获取已建立的channel
   *
   * @param addr ip:port
   * @return channel
   */
  public static Channel get(String addr) {
    return channelCache.get(addr);
  }

  /**
   * 清除addr对应的channel缓存
   *
   * @param addr ip:port
   */
  public static void remove(String addr) {
    channelCache.remove(addr);
  }

  /**
   * see {@link ConcurrentHashMap#putIfAbsent(Object, Object)}
   */
  public static Channel putIfAbsent(String addr, Channel channel) {
    return channelCache.putIfAbsent(addr, channel);
  }
}
