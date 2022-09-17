package top.haoliny.yrpc.server.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

@ConfigurationProperties(prefix = "yrpc")
@Component
@Getter
public class RpcServerConfig {
  /**
   * 服务端口
   */
  private int serverPort;
  /**
   * zookeeper server地址
   */
  private String zookeeperAddr;

  /**
   * zookeeper连接有效期
   */
  private int zookeeperSessionTimeout;

  private String zookeeperTopic;
}
