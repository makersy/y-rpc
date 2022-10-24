package io.github.makersy.yrpc.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author makersy
 * @date 2022/10/13
 * @description
 */

@AllArgsConstructor
@Getter
public enum ErrorCode {
  /**
   * ok
   */
  OK(0),
  /**
   * 参数异常
   */
  PARAM_ERROR(400),
  /**
   * 服务异常
   */
  SERVER_ERROR(500);

  private final int code;
}
