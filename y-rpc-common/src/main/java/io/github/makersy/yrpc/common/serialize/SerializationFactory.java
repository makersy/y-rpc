package io.github.makersy.yrpc.common.serialize;

/**
 * @author makersy
 * @date 2022/9/24
 * @description
 */

public class SerializationFactory {

  public static Serialization get(String serialType) {
    switch (serialType) {
      case "json":
      default:
        return new JsonSerialization();
    }
  }
}
