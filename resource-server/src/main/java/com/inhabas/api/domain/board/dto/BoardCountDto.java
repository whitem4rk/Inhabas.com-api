package com.inhabas.api.domain.board.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.inhabas.api.domain.menu.domain.valueObject.MenuType;
import com.querydsl.core.annotations.QueryProjection;

@Getter
@NoArgsConstructor
public class BoardCountDto {

  @NotNull private Integer menuId;

  @NotNull private Integer priority;

  @NotNull private MenuType type;

  @NotNull private String menuName;

  @NotNull @PositiveOrZero private Integer count;

  @Builder
  @QueryProjection
  public BoardCountDto(
      Integer menuId, Integer priority, MenuType type, String menuName, Integer count) {
    this.menuId = menuId;
    this.priority = priority;
    this.type = type;
    this.menuName = menuName;
    this.count = count;
  }
}
