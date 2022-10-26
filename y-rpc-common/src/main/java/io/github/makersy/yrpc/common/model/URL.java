package io.github.makersy.yrpc.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author makersy
 * @date 2022/9/29
 * @description
 * jackson requires object have: </p>
 * 1. no-args constructor
 * 2. getter method for all private field
 */

@Data
@NoArgsConstructor
public class URL implements Serializable {
  private static final long serialVersionUID = 3692544077592468598L;

  private Map<String, String> urlParam;
  private URLAddress urlAddress;

  public URL(URLAddress urlAddress) {
    this.urlParam = new HashMap<>();
    this.urlAddress = urlAddress;
  }

  public String getRawAddress() {
    return urlAddress == null ? null : urlAddress.getRawAddress();
  }

  public String getHost() {
    return urlAddress == null ? null : urlAddress.getHost();
  }

  public int getPort() {
    return urlAddress == null ? 0 : urlAddress.getPort();
  }

  public void addParameter(String key, String value) {
    urlParam.put(key, value);
  }

  public void addParameters(Map<String, String> params) {
    urlParam.putAll(params);
  }

  public String getParameter(String key) {
    return urlParam.get(key);
  }
}
