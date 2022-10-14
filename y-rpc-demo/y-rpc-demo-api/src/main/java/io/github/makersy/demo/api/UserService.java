package io.github.makersy.demo.api;

import io.github.makersy.demo.api.model.User;
import io.github.makersy.yrpc.common.model.Result;

/**
 * @author yhl
 * @date 2022/10/12
 * @description
 */

public interface UserService {
  Result<Boolean> addUser(User user);
}
