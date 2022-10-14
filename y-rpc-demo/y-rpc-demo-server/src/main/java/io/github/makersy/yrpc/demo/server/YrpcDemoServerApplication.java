package io.github.makersy.yrpc.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import io.github.makersy.yrpc.server.bootstrap.RpcServer;

import java.util.concurrent.CountDownLatch;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

@SpringBootApplication(scanBasePackages = {"io.github.makersy.yrpc"})
public class YrpcDemoServerApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(YrpcDemoServerApplication.class, args);
    RpcServer server = context.getBean(RpcServer.class);
    try {
      server.start();
      new CountDownLatch(1).await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
