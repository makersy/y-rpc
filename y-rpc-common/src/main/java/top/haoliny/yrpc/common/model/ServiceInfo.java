package top.haoliny.yrpc.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@Data
public class ServiceInfo implements Serializable {
  private static final long serialVersionUID = -8106879843261279092L;

  private String host;
  private int port;
}
