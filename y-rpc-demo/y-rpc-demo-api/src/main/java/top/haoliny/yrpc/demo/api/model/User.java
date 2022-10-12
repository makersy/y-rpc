package top.haoliny.yrpc.demo.api.model;

import lombok.Data;

/**
 * @author yhl
 * @date 2022/10/12
 * @description
 */

@Data
public class User {
  private Long id = -1L;
  private String name = "";
  private Integer age = 0;
}
