package io.github.makersy.yrpc.server.spring.bean;

import com.google.common.base.Preconditions;
import io.github.makersy.yrpc.common.annotation.RpcService;
import io.github.makersy.yrpc.registry.Registry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author makersy
 * @date 2022/10/24
 * @description
 * 1. 识别 {@link RpcService} 注解，将bean示例根据class name缓存 </p>
 * 2. 注册至注册中心
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class RpcServiceBeanPostProcessor implements BeanPostProcessor {

    private final RpcServiceCache rpcServiceCache;
    private final Registry registry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);

        if (rpcService != null) {
            Class<?> interfaceClass = rpcService.interfaceClass();
            Preconditions.checkState(interfaceClass != void.class, "RpcService.interfaceClass is empty!");

            // service bean cache
            rpcServiceCache.put(interfaceClass.getName(), bean);
            try {
                // register service
                registry.registerService(interfaceClass.getName());
            } catch (Exception e) {
                log.error(String.format("register service error, class name: %s", interfaceClass.getName()), e);
            }
        }
        return bean;
    }
}
