package io.github.makersy.yrpc.common.serialize;

import javax.annotation.Nullable;

/**
 * @author makersy
 * @date 2022/9/14
 * @description
 */

public interface Serialization {

  /**
   * 序列化
   *
   * @param obj 源对象
   * @return 序列化后的byte数组
   */
  @Nullable
  <T> byte[] serialize(T obj);

  /**
   * 反序列化
   *
   * @param bytes 源数据
   * @param clz   目标类型
   * @return 目标类型的对象
   */
  @Nullable
  <T> T deserialize(byte[] bytes, Class<T> clz);
}
