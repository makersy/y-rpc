package top.haoliny.yrpc.demo.api;

import top.haoliny.yrpc.common.model.Result;

/**
 * @author yhl
 * @date 2022/9/19
 * @description
 */

public interface HelloService {
   Result<String> sayHello(String user);
}
