package top.haoliny.yrpc.common.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import top.haoliny.yrpc.common.config.ZookeeperConfig;
import top.haoliny.yrpc.common.constants.Constants;

import java.net.InetAddress;

/**
 * @author yhl
 * @date 2022/9/20
 * @description 每个服务定义各自的topic，将服务provider节点信息注册到对应topic下
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yrpc.registry", name = "protocol", havingValue = "zookeeper")
public class ZookeeperRegistry0 implements Registry0 {

  private final ZookeeperConfig zkConfig;

  @Value("${server.port}")
  private int serverPort;

  @Override
  public void registerProvider() throws Throwable {
    // 初始化client
    log.info("Start to connect zookeeper, addr: {}", zkConfig.getAddr());
    CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(zkConfig.getAddr())
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .namespace(Constants.ZK_NAMESPACE)
            .build();

    client.start();

    // 创建topic节点
    String path = "/" + zkConfig.getTopic();
    Stat stat = client.checkExists().forPath(path);
    if (stat == null) {
      client.create()
              .withMode(CreateMode.PERSISTENT)
              .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
              .forPath(path);
    } else {
      log.info("zookeeper path exists: {}", path);
    }

    // 注册provider
    InetAddress addr = InetAddress.getLocalHost();
    path += "/" + addr.getHostAddress() + ":" + serverPort;
    stat = client.checkExists().forPath(path);
    if (stat == null) {
      client.create()
              .withMode(CreateMode.EPHEMERAL)
              .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
              .forPath(path);
    }
  }
}
