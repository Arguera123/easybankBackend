package com.example.easybank.util;

import com.example.easybank.domain.entity.Bill;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BillSpecifications {
    public static Specification<Bill> hasState(String state) {
        return (root, query, cb) ->
                state == null ? null : cb.equal(root.get("state"), state);
    }

    public static Specification<Bill> hasUserId(UUID userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }
}
