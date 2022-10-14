package io.github.makersy.yrpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

@ConfigurationProperties(prefix = "yrpc.server")
@Component
@Data
public class RpcServerConfig {
  /**
   * netty监听端口
   */
  private int port = 25810;
}
