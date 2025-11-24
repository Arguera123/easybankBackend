package com.example.easybank.util;

import com.example.easybank.domain.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecifications {


    //Filtra transacciones por tipo .

    public static Specification<Transaction> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    /**
     * Filtra transacciones dentro de un rango de fechas
     * Comportamiento:
     * - Si ambos parámetros son null → no aplica filtro.
     * - Si ambos existen → aplica between.
     * - Si solo 'from' existe → fecha >= from.
     * - Si solo 'to' existe → fecha <= to.
     **/
    public static Specification<Transaction> betweenDates(LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) -> {
            // Sin filtros de fechas
            if (from == null && to == null) return null;

            // Rango completo
            if (from != null && to != null) {
                return criteriaBuilder.between(root.get("dateTime"), from, to);
            }

            // Solo fecha inicial
            if (from != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), from);
            }

            // Solo fecha final
            return criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), to);
        };
    }


     //Filtra transacciones por su cuenta asociada.

    public static Specification<Transaction> hasAccount(String accountId) {
        return (root, query, criteriaBuilder) -> {
            if (accountId == null) return null;

            // Coincidencia en cualquiera de los dos extremos de la transacción
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("originAccount").get("number"), accountId),
                    criteriaBuilder.equal(root.get("destinationAccount").get("number"), accountId)
            );
        };
    }
}

