package top.haoliny.yrpc.common.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import top.haoliny.yrpc.common.config.RegistryConfig;
import top.haoliny.yrpc.common.config.ZookeeperConfig;
import top.haoliny.yrpc.common.constants.Constants;
import top.haoliny.yrpc.common.model.URLAddress;

import java.net.InetAddress;
import java.util.List;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 * todo 将provider信息注册到service级别，而不是注册到服务级别。
 */

//@Service
@Slf4j
@RequiredArgsConstructor
//@ConditionalOnProperty(prefix = "yrpc.registry", name = "protocol", havingValue = "zookeeper")
public class ZookeeperRegistry<T> implements Registry {

  private final RegistryConfig registryConfig;
  private final ZookeeperConfig zkConfig;

  @Value("${server.port}")
  private int serverPort;

  private ZooKeeper zooKeeper;

  private InstanceSerializer<URLAddress> serializer = new JsonInstanceSerializer<>(URLAddress.class);
  private ServiceDiscovery<T> serviceDiscovery;

  private ServiceCache<T> serviceCache;

  public void start() throws Throwable {
    // 初始化client
    log.info("Start to connect zookeeper, addr: {}", zkConfig.getAddr());

    CuratorFramework client = CuratorFrameworkFactory.newClient(zkConfig.getAddr(), new ExponentialBackoffRetry(1000, 3));
    client.start();
    client.create()
            .withMode(CreateMode.PERSISTENT)
            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
            .forPath(Constants.ZK_REGISTRY_PATH);

    // -------------
    // 初始化连接
    ZooKeeper zk = new ZooKeeper(
            zkConfig.getAddr(),
            Constants.ZK_SESSION_TIMEOUT,
            watchedEvent -> {
              Watcher.Event.KeeperState state = watchedEvent.getState();
              if (state == Watcher.Event.KeeperState.SyncConnected) {
                log.info("Connect zookeeper successfully, path: {}", watchedEvent.getPath());
                return;
              }

              if (state == Watcher.Event.KeeperState.Disconnected || state == Watcher.Event.KeeperState.Expired) {
                try {
                  // 自动重连
                  log.info("Trying to reconnect zookeeper, path: {}", watchedEvent.getPath());
                  start();
                } catch (Throwable t) {
                  log.error("Reconnect zk failed", t);
                }
              }
            });

    // 创建/easy-rpc节点
    String path = Constants.ZK_REGISTRY_PATH;
    if (zk.exists(path, false) == null) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 创建/easy-rpc/{topic}节点
    path += "/" + registryConfig.getTopic();
    if (zk.exists(path, false) == null) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 创建/easy-rpc/{topic}/{ip:port}节点
    InetAddress addr = InetAddress.getLocalHost();
    path += "/" + addr.getHostAddress() + ":" + serverPort;
    if (zk.exists(path, false) == null) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    this.zooKeeper = zk;
  }

  @Override
  public void registerService(String serviceName) throws Exception {

  }

  @Override
  public void unregisterService(String serviceName) throws Exception {

  }

  @Override
  public List<URLAddress> findServiceProviders(String serviceName) {
    return null;
  }
}
