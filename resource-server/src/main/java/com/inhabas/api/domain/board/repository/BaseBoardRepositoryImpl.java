package com.inhabas.api.domain.board.repository;

import static com.inhabas.api.domain.board.domain.QBaseBoard.baseBoard;
import static com.inhabas.api.domain.menu.domain.QMenu.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.inhabas.api.domain.board.dto.BoardCountDto;
import com.inhabas.api.domain.board.dto.QBoardCountDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class BaseBoardRepositoryImpl implements BaseBoardRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<BoardCountDto> countRowsGroupByMenuName(Integer menuGroupId) {
    return queryFactory
        .select(
            new QBoardCountDto(
                menu.id,
                menu.priority,
                menu.type,
                menu.name.value,
                baseBoard.id.count().intValue()))
        .from(menu)
        .leftJoin(baseBoard)
        .on(menu.id.eq(baseBoard.menu.id))
        .where(menu.menuGroup.id.eq(menuGroupId))
        .groupBy(menu.name)
        .orderBy(menu.priority.asc())
        .fetch();
  }
}
