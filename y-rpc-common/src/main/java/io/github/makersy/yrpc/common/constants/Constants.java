package io.github.makersy.yrpc.common.constants;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

public interface Constants {

  /**
   * data长度字段占用byte数
   */
  int LENGTH_FIELD_LENGTH = Integer.BYTES;

  /**
   * zk会话超时
   */
  int ZK_SESSION_TIMEOUT = 5000;

  /**
   * zk curator namespace
   */
  String ZK_NAMESPACE = "y-rpc";

  /**
   * zk注册基本路径
   */
  String ZK_REGISTRY_PATH = "/" + ZK_NAMESPACE;

  String SERIALIZATION_KEY = "serialization";
}
