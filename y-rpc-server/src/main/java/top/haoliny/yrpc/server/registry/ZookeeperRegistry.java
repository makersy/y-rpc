package top.haoliny.yrpc.server.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import top.haoliny.yrpc.common.constants.Constants;
import top.haoliny.yrpc.server.config.ZookeeperConfig;

import java.net.InetAddress;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yrpc.registry", name = "protocol", havingValue = "zookeeper")
public class ZookeeperRegistry implements Registry {

  private final ZookeeperConfig zkConfig;

  @Value("${server.port}")
  private int serverPort;

  private ZooKeeper zooKeeper;

  @Override
  public void register() throws Throwable {
    log.info("Start to connect zookeeper, addr: {}", zkConfig.getAddr());

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
                  register();
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
    path += "/" + zkConfig.getTopic();
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


}
