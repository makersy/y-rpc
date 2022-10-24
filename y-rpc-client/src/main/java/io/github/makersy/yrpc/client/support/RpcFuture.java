package io.github.makersy.yrpc.client.support;

import io.github.makersy.yrpc.common.model.RpcRequest;
import io.github.makersy.yrpc.common.model.RpcResponse;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * @author makersy
 * @date 2022/9/25
 * @description
 */

public class RpcFuture extends CompletableFuture<RpcResponse> {

  private static final ConcurrentHashMap<String, RpcFuture> FUTURE_CACHE = new ConcurrentHashMap<>();

  private final RpcRequest request;
  private final int timeout;

  public RpcFuture(@Nonnull RpcRequest request) {
    this(request, 3000);
  }

  public RpcFuture(@Nonnull RpcRequest request, int timeout) {
    this.request = request;
    this.timeout = timeout;
    FUTURE_CACHE.put(request.getRequestId(), this);
  }

  public static void addFuture(@Nonnull RpcRequest request) {
    new RpcFuture(request);
  }

  @Override
  public RpcResponse get() throws InterruptedException, ExecutionException {
    try {
      return super.get(timeout, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      FUTURE_CACHE.remove(request.getRequestId());
      return RpcResponse.buildErrorResponse(request, e);
    }
  }

  /**
   * 从缓存池拿RpcFuture
   * @param requestId requestId
   * @return requestId 对应的 RpcFuture
   */
  public static RpcFuture findById(String requestId) {
    return FUTURE_CACHE.get(requestId);
  }

  @Override
  public boolean complete(RpcResponse value) {
    return super.complete(value);
  }
}
