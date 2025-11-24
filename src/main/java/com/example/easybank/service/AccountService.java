package com.example.easybank.service;

import com.example.easybank.domain.dto.AccountCreateDTO;
import com.example.easybank.domain.dto.response.AccountResponseDTO;
import com.example.easybank.domain.entity.Account;

import java.util.Optional;

public interface AccountService {
    public void save(AccountCreateDTO accountCreate) throws Exception;
    public Optional<Account> getAccountByNumber(String accountNumber) throws Exception;
    public AccountResponseDTO getMyOwnAccount() throws Exception;
}
