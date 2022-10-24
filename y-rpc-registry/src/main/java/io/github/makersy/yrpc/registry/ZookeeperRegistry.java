package io.github.makersy.yrpc.registry;

import io.github.makersy.yrpc.common.constants.Constants;
import io.github.makersy.yrpc.common.model.URL;
import io.github.makersy.yrpc.common.model.URLAddress;
import io.github.makersy.yrpc.config.RpcServerConfig;
import io.github.makersy.yrpc.config.ZookeeperConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author makersy
 * @date 2022/9/17
 * @description
 */

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yrpc.registry", name = "protocol", havingValue = "zookeeper")
public class ZookeeperRegistry implements Registry {

  private final ZookeeperConfig zkConfig;
  private final RpcServerConfig rpcServerConfig;

  private CuratorFramework client;
  private ServiceDiscovery<URL> serviceDiscovery;
  private ConcurrentHashMap<String, ServiceCache<URL>> serviceCacheMap;

  @PostConstruct
  public void start() throws Throwable {
    // 初始化client
    log.info("Start to build zookeeper client, addr: {}", zkConfig.getAddr());

    client = CuratorFrameworkFactory.builder()
            .connectString(zkConfig.getAddr())
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    client.start();
    client.blockUntilConnected();

    log.info("Zookeeper client build success, addr: {}", zkConfig.getAddr());

    serviceDiscovery = ServiceDiscoveryBuilder.builder(URL.class)
            .basePath(Constants.ZK_REGISTRY_PATH)
            .client(client)
            .build();
    serviceDiscovery.start();

    serviceCacheMap = new ConcurrentHashMap<>();
  }

  @Override
  public void registerService(String serviceName) throws Exception {
    if (!serviceCacheMap.containsKey(serviceName)) {
      serviceDiscovery.registerService(buildServiceInstance(serviceName));
    }

    // fixme- client service cache can't be here
    ServiceCache<URL> serviceCache = serviceDiscovery.serviceCacheBuilder()
            .name(serviceName)
            .build();
    serviceCache.start();
    serviceCacheMap.put(serviceName, serviceCache);

    log.info("register service success, service name: {}", serviceName);
  }

  @Override
  public void unregisterService(String serviceName) throws Exception {
    serviceDiscovery.unregisterService(buildServiceInstance(serviceName));
  }

  private ServiceInstance<URL> buildServiceInstance(String serviceName) throws Exception {
    URLAddress urlAddress = new URLAddress(InetAddress.getLocalHost().getHostAddress(), rpcServerConfig.getPort());
    URL url = new URL(urlAddress);

    return ServiceInstance.<URL>builder()
            .name(serviceName)
            .payload(url)
            .build();
  }

  @Override
  public List<URL> findServiceProviders(String serviceName) {
    if (!serviceCacheMap.containsKey(serviceName)) {
      return Collections.emptyList();
    }
    return serviceCacheMap.get(serviceName).getInstances().stream()
            .map(ServiceInstance::getPayload)
            .collect(Collectors.toList());
  }
}
