package io.github.makersy.yrpc.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.makersy.yrpc.common.model.enums.ErrorCode;

/**
 * @author makersy
 * @date 2022/10/13
 * @description
 */

@Data
@NoArgsConstructor
public class Result<T> {

  private int code;
  private T data;
  private String message;
  private static final String SUCCESS_MESSAGE = "ok";

  public Result(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }

  public static <T> Result<T> success(T data) {
    return new Result<>(ErrorCode.OK.getCode(), data, SUCCESS_MESSAGE);
  }

  public static <T> Result<T> catchException(Throwable e) {
    return fail(ErrorCode.SERVER_ERROR, e.getMessage());
  }

  public static <T> Result<T> fail(ErrorCode errorCode, String message) {
    return new Result<>(errorCode.getCode(), null, message);
  }

  @Override
  public String toString() {
    return String.format("Result{code=%d, message=%s, data=%s}", this.code, this.message, this.data);
  }
}
