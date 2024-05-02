package com.inhabas.api.domain.contest.domain.valueObject;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import lombok.Getter;

import com.inhabas.api.auth.domain.error.businessException.InvalidInputException;

@Getter
@Embeddable
public class Topic {
  @Column(name = "TOPIC", length = 100, nullable = false)
  private String value;

  @Transient private static final int MAX_LENGTH = 100;

  public Topic() {}

  public Topic(String value) {
    if (validate(value)) this.value = value;
    else throw new InvalidInputException();
  }

  private boolean validate(Object value) {
    if (Objects.isNull(value)) return false;
    if (!(value instanceof String)) return false;

    String o = (String) value;
    if (o.isBlank()) return false;
    return o.length() < MAX_LENGTH;
  }
}
