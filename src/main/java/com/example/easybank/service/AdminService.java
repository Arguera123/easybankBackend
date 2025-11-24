package com.example.easybank.service;


import com.example.easybank.domain.dto.response.*;
import com.example.easybank.util.RoleName;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AdminService {
    List<UserResponseDTO> findAllUsers();
    void delete(UUID id);
    void changeRoles(UUID id, List<RoleName> roles);
    UserResponseDTO getUserById(UUID id);
    List<AccountResponseAdminDTO> getUserAccounts(UUID id);
    List<BillResponseDTO> getUserBills(UUID id);
    List<AdminTransactionResponseDTO> getUserTransactions(String userId);
    void depositToUserAccount(UUID userId, UUID accountId, BigDecimal amount, String description, String performedByUsername);

}
