package io.github.makersy.yrpc.common.model;

import java.io.Serializable;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

public class URLAddress implements Serializable {
  private static final long serialVersionUID = -8106879843261279092L;

  private final String host;
  private final int port;


  private transient String rawAddress;

  public URLAddress(String host, int port) {
    this.host = host;
    this.port = Math.max(port, 0);
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  /**
   * @return ip:port
   */
  public String getRawAddress() {
    if (rawAddress == null) {
      rawAddress = host + ":" + port;
    }
    return rawAddress;
  }
}
