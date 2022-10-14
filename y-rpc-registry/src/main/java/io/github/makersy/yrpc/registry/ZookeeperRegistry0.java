package io.github.makersy.yrpc.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import io.github.makersy.yrpc.common.constants.Constants;
import io.github.makersy.yrpc.config.RegistryConfig;
import io.github.makersy.yrpc.config.RpcServerConfig;
import io.github.makersy.yrpc.config.ZookeeperConfig;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

/**
 * @author yhl
 * @date 2022/9/20
 * @description 初版注册中心。每个<b>服务</b>定义各自的topic，将服务provider节点信息注册到对应topic下
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yrpc.registry", name = "protocol", havingValue = "zookeeper")
public class ZookeeperRegistry0 implements Registry0 {

  private final RegistryConfig registryConfig;
  private final ZookeeperConfig zkConfig;
  private final RpcServerConfig serverConfig;

  private CuratorFramework client;

  @PostConstruct
  public void init() {
    // 初始化client
    log.info("Start to connect zookeeper, addr: {}", zkConfig.getAddr());
    client = CuratorFrameworkFactory.builder()
            .connectString(zkConfig.getAddr())
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .namespace(Constants.ZK_NAMESPACE)
            .build();

    client.start();
  }

  @Override
  public void registerProvider() throws Throwable {
    // 创建topic节点
    String path = "/" + registryConfig.getTopic();
    Stat stat = client.checkExists().forPath(path.intern());
    if (stat == null) {
      client.create()
              .withMode(CreateMode.PERSISTENT)
              .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
              .forPath(path);
    } else {
      log.info("Zookeeper path exists: {}", path);
    }

    // 注册provider
    InetAddress addr = InetAddress.getLocalHost();
    path += "/" + addr.getHostAddress() + ":" + serverConfig.getPort();
    stat = client.checkExists().forPath(path);
    if (stat == null) {
      client.create()
              .withMode(CreateMode.EPHEMERAL)
              .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
              .forPath(path);
    }
  }

  @Override
  public List<String> getNodeList(String topic) throws Exception {
    String path = "/" + registryConfig.getTopic();
    if (client.getState() != CuratorFrameworkState.STARTED) {
      log.warn("Get {} children failed, as zookeeper client not start", path);
      return Collections.emptyList();
    }

    List<String> nodeList = client.getChildren().forPath(path);
    log.debug("Get {} children success, nodeList: {}", path, nodeList);

    return nodeList;
  }
}
