package top.haoliny.yrpc.common.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yhl
 * @date 2022/9/14
 * @description Json序列化器
 */

@Slf4j
public class JsonSerialization implements Serialization {

  private final ObjectMapper objectMapper;

  public JsonSerialization() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public <T> byte[] serialize(T obj) {
    try {
      return objectMapper.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      log.error("JsonSerialization.serialize error", e);
    }
    return null;
  }

  @Override
  public <T> T deserialize(byte[] data, Class<T> clz) {
    try {
      return objectMapper.readValue(data, clz);
    } catch (IOException e) {
      log.error("JsonSerialization.deserialize error", e);
    }
    return null;
  }
}
