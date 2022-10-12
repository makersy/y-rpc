package top.haoliny.yrpc.demo.server.service;

import lombok.extern.slf4j.Slf4j;
import top.haoliny.yrpc.common.annotation.RpcService;
import top.haoliny.yrpc.demo.api.UserService;
import top.haoliny.yrpc.demo.api.model.User;

/**
 * @author yhl
 * @date 2022/10/12
 * @description
 */

@RpcService
@Slf4j
public class UserServiceImpl implements UserService {

  @Override
  public boolean addUser(User user) {
    log.info("addUser request: {}, response: {}", user, true);
    if (user != null) {
      if (user.getAge() < 0) {
        return false;
      }
    }
    return true;
  }
}
