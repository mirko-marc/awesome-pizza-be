package com.awesomepizza.administration.specification;

import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.administration.model.OrderSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.ZoneId;

public final class OrderSpecifications {
    private OrderSpecifications() {
    }

    public static Specification<OrderDB> matching(OrderSearchCriteria criteria, ZoneId zoneId) {
        Specification<OrderDB> specification = (root, query, builder) -> builder.conjunction();

        if (criteria.getId() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("id"), criteria.getId()));
        }
        if (criteria.getOrderCode() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("orderCode"), criteria.getOrderCode()));
        }
        if (criteria.getStatus() != null) {
            specification = specification.and((root, query, builder) ->
                    builder.equal(root.get("status"), criteria.getStatus()));
        }
        if (criteria.getDay() != null) {
            Instant from = criteria.getDay().atStartOfDay(zoneId).toInstant();
            Instant to = criteria.getDay().plusDays(1).atStartOfDay(zoneId).toInstant();
            specification = specification.and((root, query, builder) ->
                    builder.and(
                            builder.greaterThanOrEqualTo(root.get("createdAt"), from),
                            builder.lessThan(root.get("createdAt"), to)));
        }

        return specification;
    }
}
