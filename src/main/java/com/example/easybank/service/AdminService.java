package com.example.easybank.service;


import com.example.easybank.domain.dto.response.*;
import com.example.easybank.util.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AdminService {

    PageResponse<UserResponseDTO> findAllUsers(Pageable pageable);
    void delete(UUID id) throws Exception;
    void changeRoles(UUID id, List<RoleName> roles);

    UserResponseDTO getUserById(UUID id);
    List<AccountResponseAdminDTO> getUserAccounts(UUID id);
    PageResponse<BillResponseDTO> getUserBills(UUID id, Pageable pageable);
    PageResponse<AdminTransactionResponseDTO> getUserTransactions(UUID userId, Pageable pageable);
    void depositToUserAccount(UUID userId, UUID accountId, BigDecimal amount, String description);
    public PageResponse<AdminTransactionResponseDTO> findAll(Pageable pageable) throws Exception;

}
