package com.example.easybank.util;

import com.example.easybank.domain.entity.Bill;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BillSpecifications {

    //Crear una Specification para filtrar facturas por estado.
    public static Specification<Bill> hasState(String state) {
        return (root, query, cb) ->
                // Si el parámetro es null, se devuelve null, lo que significa "no aplicar filtro"
                state == null ? null : cb.equal(root.get("state"), state);
    }

    //Crea una Specification para filtrar facturas por ID de usuario
    public static Specification<Bill> hasUserId(UUID userId) {
        return (root, query, cb) ->
                // Si el parámetro es null, no se aplica filtro
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }
}

