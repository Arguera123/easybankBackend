package com.example.easybank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID> {
    default public T findByIdOrThrow(ID id) throws Exception {
        return findById(id).orElseThrow(() -> new Exception(getClassName() + " not found"));
    }

    private String getClassName() {
        return this.getClass()
                .getGenericInterfaces()[0]
                .getTypeName()
                .replace("com.example.easybank.Repository.iGenericRepository<", "")
                .replace(">", "");
    }
}
