package top.haoliny.yrpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

@ConfigurationProperties(prefix = "yrpc.registry.zookeeper")
@Component
@Data
public class ZookeeperConfig {
  /**
   * zookeeper server地址
   */
  private String addr = "127.0.0.1:2181";

  /**
   * zookeeper连接有效期
   */
  private int sessionTimeout = 5000;
}
