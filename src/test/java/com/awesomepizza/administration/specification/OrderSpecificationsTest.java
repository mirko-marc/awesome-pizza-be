package com.awesomepizza.administration.specification;

import com.awesomepizza.administration.model.OrderSearchCriteria;
import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.enumeration.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderSpecificationsTest {

    @Test
    @SuppressWarnings("unchecked")
    void combinesEveryFilterAndUsesBusinessTimezoneDayBoundaries() {
        Root<OrderDB> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        Path<Object> genericPath = mock(Path.class);
        Path<Instant> createdAtPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        UUID orderCode = UUID.randomUUID();
        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .id(42L)
                .orderCode(orderCode)
                .day(LocalDate.of(2026, 9, 23))
                .status(OrderStatus.RECEIVED)
                .build();
        when(root.get("id")).thenReturn(genericPath);
        when(root.get("orderCode")).thenReturn(genericPath);
        when(root.get("status")).thenReturn(genericPath);
        when(root.<Instant>get("createdAt")).thenReturn(createdAtPath);
        when(builder.conjunction()).thenReturn(predicate);
        when(builder.equal(any(), any())).thenReturn(predicate);
        when(builder.greaterThanOrEqualTo(any(), any(Instant.class))).thenReturn(predicate);
        when(builder.lessThan(any(), any(Instant.class))).thenReturn(predicate);
        when(builder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        OrderSpecifications.matching(criteria, ZoneId.of("Europe/Rome"))
                .toPredicate(root, query, builder);

        verify(builder).equal(genericPath, 42L);
        verify(builder).equal(genericPath, orderCode);
        verify(builder).equal(genericPath, OrderStatus.RECEIVED);
        verify(builder).greaterThanOrEqualTo(
                createdAtPath, Instant.parse("2026-09-22T22:00:00Z"));
        verify(builder).lessThan(
                createdAtPath, Instant.parse("2026-09-23T22:00:00Z"));
    }
}
