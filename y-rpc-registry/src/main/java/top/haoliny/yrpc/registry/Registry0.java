package top.haoliny.yrpc.registry;

import java.util.List;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

public interface Registry0 {

  /**
   * 注册至注册中心
   */
  void registerProvider() throws Throwable;

  /**
   * 获取topic下的provider节点
   * @param topic 每个服务是一个topic
   * @return 服务的所有provider
   */
  List<String> getNodeList(String topic) throws Exception;
}
