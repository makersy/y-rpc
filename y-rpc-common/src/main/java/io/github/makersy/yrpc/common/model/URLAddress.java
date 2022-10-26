package io.github.makersy.yrpc.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author makersy
 * @date 2022/9/20
 * @description
 */

@Data
@NoArgsConstructor
public class URLAddress implements Serializable {
  private static final long serialVersionUID = -8106879843261279092L;

  private String host;
  private int port;

  private transient String rawAddress;

  public URLAddress(String host, int port) {
    this.host = host;
    this.port = Math.max(port, 0);
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
