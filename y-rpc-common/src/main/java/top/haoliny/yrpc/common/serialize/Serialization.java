package top.haoliny.yrpc.common.serialize;

/**
 * @author yhl
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
  <T> byte[] serialize(T obj);

  /**
   * 反序列化
   *
   * @param bytes 源数据
   * @param clz   目标类型
   * @return 目标类型的对象
   */
  <T> T deserialize(byte[] bytes, Class<T> clz);
}
