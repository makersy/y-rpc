package io.github.makersy.yrpc.registry;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
  private Cache<String, ServiceCache<URL>> serviceCacheMap;

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

    // 服务发现
    serviceDiscovery = ServiceDiscoveryBuilder.builder(URL.class)
            .basePath(Constants.ZK_REGISTRY_PATH)
            .client(client)
            .serializer(new JsonInstanceSerializer<>(URL.class))
            .build();
    serviceDiscovery.start();

    // 服务缓存
    serviceCacheMap = Caffeine.newBuilder().build();
  }

  @Override
  public void registerService(String serviceName) throws Exception {
    serviceDiscovery.registerService(buildServiceInstance(serviceName));
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
  public URL findServiceProvider(String serviceName) {
    ServiceCache<URL> service = serviceCacheMap.get(serviceName, str -> buildServiceCache(serviceName));
    if (service == null) {
      return null;
    }

    List<ServiceInstance<URL>> instances = service.getInstances();
    // 随机一个节点
    // todo 负载均衡
    ServiceInstance<URL> instance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    log.info("service url: {}", instance.getPayload());

    return instance.getPayload();
  }

  private ServiceCache<URL> buildServiceCache(String serviceName) {
    try {
      ServiceCache<URL> serviceCache = serviceDiscovery.serviceCacheBuilder()
              .name(serviceName)
              .build();
      serviceCache.start();
      log.info("service cache start successfully, serviceName: {}", serviceName);
      return serviceCache;
    } catch (Exception e) {
      log.error("service cache start failed, serviceName: {}", serviceName, e);
      return null;
    }
  }
}
