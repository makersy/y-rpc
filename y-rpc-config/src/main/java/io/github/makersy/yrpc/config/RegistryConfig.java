package io.github.makersy.yrpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author makersy
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

  /**
   * 标识 provider
   */
  private String topic = "testService";
}
