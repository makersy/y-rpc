package top.haoliny.yrpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yhl
 * @date 2022/9/20
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfo implements Serializable {
  private static final long serialVersionUID = -8106879843261279092L;

  private String host;
  private int port;
}
