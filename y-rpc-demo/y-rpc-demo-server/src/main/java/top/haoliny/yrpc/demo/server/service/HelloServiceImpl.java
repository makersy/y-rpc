package top.haoliny.yrpc.demo.server.service;

import top.haoliny.yrpc.demo.api.HelloService;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

public class HelloServiceImpl implements HelloService {
  @Override
  public String sayHello(String user) {
    return "Hello, " + user;
  }
}
