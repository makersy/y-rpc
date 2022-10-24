package io.github.makersy.yrpc.server.spring.bean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author makersy
 * @date 2022/10/24
 * @description
 */

@Component
public class RpcServiceCache {
    private Map<String, Object> beanCache;

    @PostConstruct
    public void init() {
        beanCache = new HashMap<>(16);
    }

    public Object put(String className, Object bean) {
        return beanCache.put(className, bean);
    }

    public Object get(String className) {
        return beanCache.get(className);
    }
}
