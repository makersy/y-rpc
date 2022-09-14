package top.haoliny.yrpc.common.model;

import lombok.Data;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 */

@Data
public class RpcRequest {
  /**
   * 请求编号
   */
  private String requestId;
  /**
   * 类全限定名
   */
  private String className;
  /**
   * 方法名
   */
  private String methodName;
  /**
   * 请求参数
   */
  private Object[] parameters;
}
