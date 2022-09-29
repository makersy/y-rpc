package top.haoliny.yrpc.common.registry;

import top.haoliny.yrpc.common.model.URLAddress;

import java.util.List;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

public interface Registry {

  void registerService(String serviceName) throws Exception;

  void unregisterService(String serviceName) throws Exception;

  List<URLAddress> findServiceProviders(String serviceName);
}
