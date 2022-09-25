package top.haoliny.yrpc.client.support;

import top.haoliny.yrpc.common.model.RpcRequest;
import top.haoliny.yrpc.common.model.RpcResponse;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * @author yhl
 * @date 2022/9/25
 * @description
 */

public class RpcFuture extends CompletableFuture<RpcResponse> {

  private static final ConcurrentHashMap<String, RpcFuture> futureCache = new ConcurrentHashMap<>();

  private final RpcRequest request;
  private final int timeout;

  public RpcFuture(@Nonnull RpcRequest request) {
    this(request, 3000);
  }

  public RpcFuture(@Nonnull RpcRequest request, int timeout) {
    this.request = request;
    this.timeout = timeout;
  }

  @Override
  public RpcResponse get() throws InterruptedException, ExecutionException {
    try {
      return super.get(timeout, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      futureCache.remove(request.getRequestId());
      return RpcResponse.buildErrorResponse(request, e);
    }
  }

  /**
   * 从缓存池拿RpcFuture
   * @param requestId requestId
   * @return requestId 对应的 RpcFuture
   */
  public RpcFuture getFuture(String requestId) {
    return futureCache.get(requestId);
  }
}
