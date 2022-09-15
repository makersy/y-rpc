package top.haoliny.yrpc.common.model;

import lombok.Data;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 */

@Data
public class RpcResponse {
  /**
   * 请求编号
   */
  private String requestId;
  /**
   * 抛出的异常
   */
  private Throwable throwable;
  /**
   * 返回值
   */
  private Object result;
}
