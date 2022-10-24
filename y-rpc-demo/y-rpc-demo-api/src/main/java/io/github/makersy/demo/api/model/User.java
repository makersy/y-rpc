package io.github.makersy.demo.api.model;

import lombok.Data;

/**
 * @author makersy
 * @date 2022/10/12
 * @description
 */

@Data
public class User {
  private Long id = -1L;
  private String name = "";
  private Integer age = 0;
}
