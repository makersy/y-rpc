package io.github.makersy.yrpc.common.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import io.github.makersy.yrpc.common.model.RpcRequest;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkState;

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
      return null;
    }
  }

  @Override
  public <T> T deserialize(byte[] data, Class<T> clz) {
    try {
      T obj = objectMapper.readValue(data, clz);
      if (obj instanceof RpcRequest) {
        handleRequest((RpcRequest) obj);
      }
      return obj;
    } catch (Exception e) {
      log.error("JsonSerialization.deserialize error", e);
    }
    return null;
  }

  /**
   * 由于json反序列化时不记录对象信息，因此Object[]数组内如果是非基本类型，如对象等，会被反序列化为map，和真实类型对不上
   *
   * @param request {@link RpcRequest} object
   */
  private void handleRequest(RpcRequest request) throws IOException {
    if (ArrayUtils.isNotEmpty(request.getParameterTypes())) {
      Class<?>[] parameterTypes = request.getParameterTypes();
      Object[] parameters = request.getParameters();

      checkState(ArrayUtils.isSameLength(parameterTypes, parameters),
              "RpcRequest parameter types length mismatch");

      for (int i = 0; i < parameterTypes.length; i++) {
        byte[] bytes = objectMapper.writeValueAsBytes(parameters[i]);
        parameters[i] = objectMapper.readValue(bytes, parameterTypes[i]);
      }
    }
  }
}
