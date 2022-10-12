package top.haoliny.yrpc.demo.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import top.haoliny.yrpc.common.annotation.RpcReference;
import top.haoliny.yrpc.demo.api.HelloService;
import top.haoliny.yrpc.demo.api.UserService;
import top.haoliny.yrpc.demo.api.model.User;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yhl
 * @date 2022/10/10
 * @description
 */

@Controller
@Slf4j
public class CallServerService {

  @RpcReference
  private HelloService helloService;

  @RpcReference
  private UserService userService;

  private AtomicInteger id = new AtomicInteger();

  @GetMapping("/hello/{name}")
  @ResponseBody
  public String callHello(@PathVariable("name") String name) {
    log.info("callHello request: {}", name);
    String resp = helloService.sayHello(name);
    log.info("callHello response: {}", resp);
    return resp;
  }

  @GetMapping("/addUser")
  @ResponseBody
  public String callAddUser(@RequestParam("age") Integer age, @RequestParam("name") String name) {
    log.info("callAddUser request age: {} name: {}", age, name);

    User user = new User();
    user.setId((long) id.addAndGet(1));
    user.setName(name);
    user.setAge(age);
    boolean resp = userService.addUser(user);

    log.info("callAddUser response: {}", resp);
    return Boolean.toString(resp);
  }
}
