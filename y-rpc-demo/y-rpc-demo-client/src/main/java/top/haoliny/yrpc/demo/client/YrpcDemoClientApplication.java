package top.haoliny.yrpc.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yhl
 * @date 2022/9/30
 * @description
 */

@SpringBootApplication(scanBasePackages = {"top.haoliny.yrpc"})
public class YrpcDemoClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(YrpcDemoClientApplication.class, args);
  }
}
