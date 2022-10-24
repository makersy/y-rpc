package io.github.makersy.yrpc.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author makersy
 * @date 2022/9/30
 * @description
 */

@SpringBootApplication(scanBasePackages = {"io.github.makersy.yrpc"})
public class YrpcDemoClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(YrpcDemoClientApplication.class, args);
  }
}
