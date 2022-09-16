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
   * 请求id
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
   * 参数类型
   */
  private Class<?>[] parameterTypes;
  /**
   * 参数
   */
  private Object[] parameters;
}
