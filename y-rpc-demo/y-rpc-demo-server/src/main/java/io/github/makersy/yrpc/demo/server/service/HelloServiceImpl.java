package io.github.makersy.yrpc.demo.server.service;

import io.github.makersy.yrpc.common.annotation.RpcService;
import io.github.makersy.yrpc.common.model.Result;
import io.github.makersy.demo.api.HelloService;

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
