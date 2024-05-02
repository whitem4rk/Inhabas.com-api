package com.inhabas.api.domain.normalBoard.repository;

import static com.inhabas.api.auth.domain.oauth2.member.domain.entity.QMember.member;
import static com.inhabas.api.domain.file.domain.QBoardFile.boardFile;
import static com.inhabas.api.domain.normalBoard.domain.QNormalBoard.normalBoard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.inhabas.api.domain.normalBoard.domain.NormalBoard;
import com.inhabas.api.domain.normalBoard.domain.NormalBoardType;
import com.inhabas.api.domain.normalBoard.dto.NormalBoardDto;
import com.inhabas.api.domain.normalBoard.dto.QNormalBoardDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class NormalBoardRepositoryImpl implements NormalBoardRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<NormalBoardDto> findAllByTypeAndIsPinned(NormalBoardType boardType) {
    return queryFactory
        .select(
            new QNormalBoardDto(
                normalBoard.id,
                normalBoard.title.value,
                normalBoard.writer.id,
                normalBoard.writer.name.value,
                normalBoard.datePinExpiration,
                normalBoard.dateCreated,
                normalBoard.dateUpdated,
                normalBoard.isPinned))
        .from(normalBoard)
        .leftJoin(normalBoard.writer, member)
        .fetchJoin()
        .where(
            eqNormalBoardType(boardType)
                .and(normalBoard.isPinned.isTrue())
                .and(normalBoard.datePinExpiration.after(LocalDateTime.now())))
        .orderBy(normalBoard.dateCreated.desc())
        .fetch();
  }

  @Override
  public List<NormalBoardDto> findAllByMemberIdAndTypeAndSearch(
      Long memberId, NormalBoardType boardType, String search) {
    return queryFactory
        .select(
            new QNormalBoardDto(
                normalBoard.id,
                normalBoard.title.value,
                normalBoard.writer.id,
                normalBoard.writer.name.value,
                normalBoard.datePinExpiration,
                normalBoard.dateCreated,
                normalBoard.dateUpdated,
                normalBoard.isPinned))
        .from(normalBoard)
        .leftJoin(normalBoard.writer, member)
        .fetchJoin()
        .where(
            eqMemberId(memberId)
                .and(eqNormalBoardType(boardType))
                .and(likeTitle(search).or(likeContent(search))))
        .orderBy(normalBoard.dateCreated.desc())
        .fetch();
  }

  @Override
  public List<NormalBoardDto> findAllByTypeAndSearch(NormalBoardType boardType, String search) {
    return queryFactory
        .select(
            new QNormalBoardDto(
                normalBoard.id,
                normalBoard.title.value,
                normalBoard.writer.id,
                normalBoard.writer.name.value,
                normalBoard.datePinExpiration,
                normalBoard.dateCreated,
                normalBoard.dateUpdated,
                normalBoard.isPinned))
        .from(normalBoard)
        .leftJoin(normalBoard.writer, member)
        .fetchJoin()
        .where(eqNormalBoardType(boardType).and(likeTitle(search).or(likeContent(search))))
        .orderBy(normalBoard.dateCreated.desc())
        .fetch();
  }

  @Override
  public Optional<NormalBoard> findByMemberIdAndTypeAndId(
      Long memberId, NormalBoardType boardType, Long boardId) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(normalBoard)
            .leftJoin(normalBoard.writer, member)
            .fetchJoin()
            .leftJoin(normalBoard.files, boardFile)
            .fetchJoin()
            .where(
                eqMemberId(memberId)
                    .and(eqNormalBoardType(boardType))
                    .and(normalBoard.id.eq(boardId)))
            .orderBy(normalBoard.dateCreated.desc())
            .fetchOne());
  }

  @Override
  public Optional<NormalBoard> findByTypeAndId(NormalBoardType boardType, Long boardId) {
    return Optional.ofNullable(
        queryFactory
            .selectFrom(normalBoard)
            .where((eqNormalBoardType(boardType)).and(normalBoard.id.eq(boardId)))
            .orderBy(normalBoard.dateCreated.desc())
            .fetchOne());
  }

  private BooleanExpression eqMemberId(Long memberId) {
    return normalBoard.writer.id.eq(memberId);
  }

  private BooleanExpression eqNormalBoardType(NormalBoardType normalBoardType) {
    return normalBoard.menu.id.eq(normalBoardType.getMenuId());
  }

  private BooleanExpression likeTitle(String search) {
    return normalBoard.title.value.like("%" + search + "%");
  }

  private BooleanExpression likeContent(String search) {
    return normalBoard.content.value.like("%" + search + "%");
  }
}
