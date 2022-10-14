package io.github.makersy.yrpc.common.exception;

/**
 * @author yhl
 * @date 2022/9/25
 * @description
 */

public class ExceptionCode {
  /**
   * consumer连接出错
   */
  public static final int CLIENT_CONNECT_FAILED = 1;

  /**
   * 无provider节点
   */
  public static final int NO_PROVIDERS = 2;

  /**
   * consumer内部错误
   */
  public static final int CLIENT_INTERNAL_ERROR = 3;
}
