package io.github.makersy.yrpc.common.model;

import lombok.Data;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 */

@Data
public class RpcRequest {

  private static final AlternativeJdkIdGenerator idGenerator = new AlternativeJdkIdGenerator();

  public RpcRequest() {
    this.requestId = idGenerator.generateId().toString().replace("-", "");
  }

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
