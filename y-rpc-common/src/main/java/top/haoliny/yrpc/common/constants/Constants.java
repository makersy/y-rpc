package top.haoliny.yrpc.common.constants;

/**
 * @author yhl
 * @date 2022/9/15
 * @description
 */

public class Constants {

  /**
   * data长度字段占用byte数
   */
  public static final int LENGTH_FIELD_LENGTH = Integer.BYTES;
  /**
   * zk会话超时
   */
  public static final int ZK_SESSION_TIMEOUT = 5000;

  /**
   * zk curator namespace
   */
  public static final String ZK_NAMESPACE = "y-rpc";

  /**
   * zk注册基本路径
   */
  public static final String ZK_REGISTRY_PATH = "/" + ZK_NAMESPACE;

}
