package top.haoliny.yrpc.server.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.haoliny.yrpc.common.config.RegistryConfig;
import top.haoliny.yrpc.common.config.ZookeeperConfig;
import top.haoliny.yrpc.common.registry.ZookeeperRegistry0;

/**
 * @author yhl
 * @date 2022/10/10
 * @description
 */

public class ZkTest {

  private RegistryConfig registryConfig;
  private ZookeeperRegistry0 registry0;

  @BeforeEach
  public void beforeEach() {
    registryConfig = new RegistryConfig();

    ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
    zookeeperConfig.setAddr("127.0.0.1:2181");

    registry0 = new ZookeeperRegistry0(registryConfig, zookeeperConfig);

    registry0.init();
  }
  @Test
  public void testZkGetNodes() {
    try {
      registry0.registerProvider();

      System.out.println(registry0.getNodeList(registryConfig.getTopic()));
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
