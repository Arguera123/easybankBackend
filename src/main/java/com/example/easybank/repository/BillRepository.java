package com.example.easybank.repository;

import com.example.easybank.domain.entity.Bill;
import com.example.easybank.domain.entity.UserData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillRepository extends JpaRepository<Bill, UUID> {
    public List<Bill> getBillsById(UUID id);
    public List<Bill> findBillsByStateAndUser(String state, UserData user);
    public Page<Bill> getBillsByUser(UserData userData, Pageable pageable);
    public Page<Bill> findAll(Specification<Bill> spec, Pageable pageable);
}
