package top.haoliny.yrpc.demo.server.service;

import top.haoliny.yrpc.common.annotation.RpcService;
import top.haoliny.yrpc.common.model.Result;
import top.haoliny.yrpc.demo.api.HelloService;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

@RpcService
public class HelloServiceImpl implements HelloService {
  @Override
  public Result<String> sayHello(String user) {
    return Result.success("Hello, " + user);
  }
}
