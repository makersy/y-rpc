package top.haoliny.yrpc.demo.api;

import top.haoliny.yrpc.common.model.Result;
import top.haoliny.yrpc.demo.api.model.User;

/**
 * @author yhl
 * @date 2022/10/12
 * @description
 */

public interface UserService {
  Result<Boolean> addUser(User user);
}
