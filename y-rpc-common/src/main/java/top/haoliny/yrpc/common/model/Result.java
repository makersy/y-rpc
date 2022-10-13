package top.haoliny.yrpc.common.model;

import lombok.Data;
import top.haoliny.yrpc.common.model.enums.ErrorCode;

/**
 * @author yhl
 * @date 2022/10/13
 * @description
 */

@Data
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
