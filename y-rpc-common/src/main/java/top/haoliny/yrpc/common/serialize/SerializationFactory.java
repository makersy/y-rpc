package top.haoliny.yrpc.common.serialize;

/**
 * @author yhl
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
