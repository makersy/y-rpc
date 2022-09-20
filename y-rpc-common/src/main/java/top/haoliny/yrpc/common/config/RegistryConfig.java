package top.haoliny.yrpc.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

@ConfigurationProperties(prefix = "yrpc.registry")
@Component
@Data
public class RegistryConfig {
  /**
   * 注册中心协议
   */
  private String protocol = "zookeeper";
}