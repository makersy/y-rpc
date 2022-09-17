package top.haoliny.yrpc.server.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;
import top.haoliny.yrpc.common.constants.Constants;
import top.haoliny.yrpc.server.config.RpcServerConfig;

import java.net.InetAddress;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ZookeeperRegistry implements Registry {

  private final RpcServerConfig config;

  @Override
  public void register() throws Throwable {
    // 初始化连接
    ZooKeeper zk = new ZooKeeper(
            config.getZookeeperAddr(),
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
    path += "/" + config.getZookeeperTopic();
    if (zk.exists(path, false) == null) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 创建/easy-rpc/{topic}/{ip:port}节点
    InetAddress addr = InetAddress.getLocalHost();
    path += "/" + addr.getHostAddress() + config.getServerPort();
    if (zk.exists(path, false) == null) {
      zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }
  }


}
