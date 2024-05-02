package com.inhabas.api.domain.menu.domain.valueObject;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Getter;

@Getter
@Embeddable
public class MenuName {
  @Column(name = "NAME", length = 15, nullable = false)
  private String value;

  @Transient private static final int MAX_LENGTH = 15;

  public MenuName() {}

  public MenuName(String value) {
    if (validate(value)) this.value = value;
    else throw new IllegalArgumentException();
  }

  private boolean validate(Object value) {
    if (Objects.isNull(value)) return false;
    if (!(value instanceof String)) return false;

    String o = (String) value;
    if (o.isBlank()) return false;
    return o.length() < MAX_LENGTH;
  }
}
