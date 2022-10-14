package io.github.makersy.yrpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/24
 * @description
 */

@ConfigurationProperties(prefix = "yrpc.protocol")
@Component
@Data
public class ProtocolConfig {
  /**
   * 序列化方式，目前仅支持json
   */
  private String serialization = "json";
}
