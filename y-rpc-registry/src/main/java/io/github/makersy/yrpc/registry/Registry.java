package io.github.makersy.yrpc.registry;

import io.github.makersy.yrpc.common.model.URL;

import java.util.List;

/**
 * @author makersy
 * @date 2022/9/20
 * @description
 */

public interface Registry {

  void registerService(String serviceName) throws Exception;

  void unregisterService(String serviceName) throws Exception;

  /**
   * @param serviceName interface name
   * @return available provider urls
   */
  List<URL> findServiceProviders(String serviceName);
}
