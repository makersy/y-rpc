package top.haoliny.yrpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yhl
 * @date 2022/9/17
 * @description
 */

@ConfigurationProperties(prefix = "yrpc")
@Component
@Data
public class RpcServerConfig {
}
