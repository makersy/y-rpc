package top.haoliny.yrpc.demo.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.haoliny.yrpc.client.proxy.ProxyFactory;
import top.haoliny.yrpc.common.annotation.RpcReference;
import top.haoliny.yrpc.demo.api.HelloService;

/**
 * @author yhl
 * @date 2022/10/10
 * @description
 */

@Controller
@Slf4j
public class CallHelloService {

  @RpcReference
  private HelloService helloService;

  @RequestMapping("/hello/{name}")
  @ResponseBody
  public String callHello(@PathVariable("name") String name) {
    log.info("request: {}", name);
    helloService = ProxyFactory.getProxyInstance(HelloService.class);
    String resp = helloService.sayHello(name);
    log.info("server response: {}", resp);
    return resp;
  }
}
