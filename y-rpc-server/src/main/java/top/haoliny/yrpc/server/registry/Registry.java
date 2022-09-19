package top.haoliny.yrpc.server.registry;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

public interface Registry {

  /**
   * 注册至注册中心
   */
  abstract void register() throws Throwable;
}
