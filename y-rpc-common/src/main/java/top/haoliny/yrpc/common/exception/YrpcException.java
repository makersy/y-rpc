package top.haoliny.yrpc.common.exception;

/**
 * @author yhl
 * @date 2022/9/25
 * @description
 */

public class YrpcException extends Exception {

  /**
   * 异常码
   */
  public final int code;

  public YrpcException(int code) {
    this.code = code;
  }

  public YrpcException(int code, String message) {
    super(message);
    this.code = code;
  }

  public YrpcException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public YrpcException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }
}
