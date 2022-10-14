package io.github.makersy.yrpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.makersy.yrpc.server.support.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@Configuration
@ConfigurationProperties(prefix = "yrpc.server.thread")
@Data
public class ThreadPoolConfig {

  /**
   * todo 线程池类型，目前默认fixed
   */
  private String threadPool = "fixed";
  /**
   * 最大线程数
   */
  private int threads = 100;
  /**
   * 队列长度
   */
  private int queues = 0;

  @Bean
  public ExecutorService serverThreadPool() {
    BlockingQueue<Runnable> blockingQueue;
    if (queues == 0) {
      blockingQueue = new SynchronousQueue<>();
    } else if (queues < 0) {
      //todo avoid OOM
      blockingQueue = new LinkedBlockingQueue<>();
    } else {
      blockingQueue = new LinkedBlockingQueue<>(queues);
    }
    return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
            blockingQueue, new NamedThreadFactory("RpcServer"));
  }
}
