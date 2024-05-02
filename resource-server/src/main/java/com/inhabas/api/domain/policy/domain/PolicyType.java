package com.inhabas.api.domain.policy.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.inhabas.api.domain.board.domain.valueObject.Title;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PolicyType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Title title;

  public PolicyType(String title) {
    this.title = new Title(title);
  }

  public String getTitle() {
    return title.getValue();
  }
}
