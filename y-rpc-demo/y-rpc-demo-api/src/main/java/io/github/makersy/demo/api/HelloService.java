package io.github.makersy.demo.api;

import io.github.makersy.yrpc.common.model.Result;

/**
 * @author makersy
 * @date 2022/9/19
 * @description
 */

public interface HelloService {
   Result<String> sayHello(String user);
}
