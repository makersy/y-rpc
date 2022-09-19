package top.haoliny.yrpc.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import top.haoliny.yrpc.server.bootstrap.RpcServer;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

@SpringBootApplication(scanBasePackages = {"top.haoliny.yrpc"})
public class YrpcDemoServerApplication {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(YrpcDemoServerApplication.class, args);
    RpcServer server = context.getBean(RpcServer.class);
    server.start();
  }
}
