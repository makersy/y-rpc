package io.github.makersy.yrpc.server.test;

import io.github.makersy.yrpc.config.RegistryConfig;
import io.github.makersy.yrpc.config.RpcServerConfig;
import io.github.makersy.yrpc.config.ZookeeperConfig;
import io.github.makersy.yrpc.registry.ZookeeperRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author makersy
 * @date 2022/10/10
 * @description
 */

public class ZkTest {

  private RegistryConfig registryConfig;
  private ZookeeperRegistry registry;

  @BeforeEach
  public void beforeEach() {
    registryConfig = new RegistryConfig();

    ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
    zookeeperConfig.setAddr("127.0.0.1:2181");

    RpcServerConfig serverConfig = new RpcServerConfig();

    registry = new ZookeeperRegistry(zookeeperConfig, serverConfig);

    try {
      registry.start();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
  @Test
  public void testZkGetNodes() {
    try {

    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
