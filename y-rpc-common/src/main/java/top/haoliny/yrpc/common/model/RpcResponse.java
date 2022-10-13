package top.haoliny.yrpc.common.model;

import lombok.Data;

/**
 * @author yhl
 * @date 2022/9/14
 * @description
 */

@Data
public class RpcResponse {

  /**
   * 请求编号
   */
  private String requestId;
  /**
   * 抛出的异常
   */
  private Throwable throwable;
  /**
   * 返回值
   */
  private Result<?> result;

  /**
   * @return 错误响应
   */
  public static RpcResponse buildErrorResponse(RpcRequest request, Throwable t) {
    RpcResponse response = new RpcResponse();
    response.setRequestId(request.getRequestId());
    response.setThrowable(t);
    return response;
  }
}
