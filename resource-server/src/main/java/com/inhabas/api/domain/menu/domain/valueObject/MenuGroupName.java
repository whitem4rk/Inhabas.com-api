package com.inhabas.api.domain.menu.domain.valueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Getter;

import com.inhabas.api.auth.domain.error.businessException.InvalidInputException;

@Getter
@Embeddable
public class MenuGroupName {

  @Column(name = "NAME", length = 10, nullable = false)
  private String value;

  @Transient private static final int MAX_LENGTH = 10;

  public MenuGroupName() {}

  public MenuGroupName(String value) {
    if (validate(value)) this.value = value;
    else throw new InvalidInputException();
  }

  private boolean validate(Object value) {
    if (value == null) return false;
    if (!(value instanceof String)) return false;

    String o = (String) value;
    if (o.isBlank()) return false;
    return o.length() < MAX_LENGTH;
  }
}
