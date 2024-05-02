package com.inhabas.api.domain.budget.repository;

import static com.inhabas.api.auth.domain.oauth2.member.domain.entity.QMember.member;
import static com.inhabas.api.domain.budget.domain.QBudgetSupportApplication.budgetSupportApplication;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.inhabas.api.auth.domain.oauth2.member.domain.valueObject.RequestStatus;
import com.inhabas.api.domain.budget.dto.BudgetApplicationDto;
import com.inhabas.api.domain.budget.dto.QBudgetApplicationDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class BudgetApplicationRepositoryImpl implements BudgetApplicationRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<BudgetApplicationDto> search(RequestStatus status) {

    return queryFactory
        .select(
            new QBudgetApplicationDto(
                budgetSupportApplication.id,
                budgetSupportApplication.title.value,
                budgetSupportApplication.applicant,
                budgetSupportApplication.dateCreated,
                budgetSupportApplication.status))
        .from(budgetSupportApplication)
        .leftJoin(budgetSupportApplication.applicant, member)
        .fetchJoin()
        .where(sameStatus(status))
        .orderBy(
            budgetSupportApplication.dateUsed.desc(), budgetSupportApplication.dateCreated.desc())
        .fetch();
  }

  private BooleanExpression sameStatus(RequestStatus status) {
    return status == null
        ? Expressions.asBoolean(true).isTrue()
        : budgetSupportApplication.status.eq(status);
  }

  // 쿼리 결과 EHCACHE 등으로 캐시할 필요가 있음. 카운트 쿼리가 비효율적임.
  private Integer getCount(RequestStatus status) {
    return queryFactory
        .selectFrom(budgetSupportApplication)
        .where(sameStatus(status))
        .fetch()
        .size();
  }
}
