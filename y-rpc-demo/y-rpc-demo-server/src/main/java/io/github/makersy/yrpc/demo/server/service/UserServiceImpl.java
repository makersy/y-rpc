package io.github.makersy.yrpc.demo.server.service;

import lombok.extern.slf4j.Slf4j;
import io.github.makersy.yrpc.common.annotation.RpcService;
import io.github.makersy.yrpc.common.model.Result;
import io.github.makersy.demo.api.UserService;
import io.github.makersy.demo.api.model.User;

/**
 * @author yhl
 * @date 2022/10/12
 * @description
 */

@RpcService
@Slf4j
public class UserServiceImpl implements UserService {

  @Override
  public Result<Boolean> addUser(User user) {
    log.info("addUser request: {}, response: {}", user, true);
    if (user != null) {
      if (user.getAge() < 0) {
        return Result.success(false);
      }
    }
    return Result.success(true);
  }
}
