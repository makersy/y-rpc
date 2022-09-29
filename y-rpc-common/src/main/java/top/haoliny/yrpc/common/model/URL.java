package top.haoliny.yrpc.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yhl
 * @date 2022/9/29
 * @description
 */

public class URL implements Serializable {
  private static final long serialVersionUID = 3692544077592468598L;

  private final Map<String, String> urlParam;
  private final URLAddress urlAddress;

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
