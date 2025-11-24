package com.example.easybank.util;

import com.example.easybank.domain.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecifications {
    public static Specification<Transaction> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Transaction> betweenDates(LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) -> {
            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return criteriaBuilder.between(root.get("dateTime"), from, to);
            }

            if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), from);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), to);
        };
    }

    public static Specification<Transaction> hasAccount(String accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null) return null;
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("originAccount").get("number"), accountId),
                    criteriaBuilder.equal(root.get("destinationAccount").get("number"), accountId)
            );
        };
    }
}
