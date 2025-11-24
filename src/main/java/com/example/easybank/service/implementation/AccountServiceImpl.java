package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.AccountCreateDTO;
import com.example.easybank.domain.dto.response.AccountResponseDTO;
import com.example.easybank.domain.mapper.AccountMapper;
import com.example.easybank.exception.EmptyCardListException;
import com.example.easybank.service.AccountService;
import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.exception.AlreadyExistsException;
import com.example.easybank.exception.ModelNotFoundException;
import com.example.easybank.exception.StorageException;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    // Repositorios necesarios para acceder a cuentas y usuarios
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void save(AccountCreateDTO accountCreate) throws Exception {

        // Verifica si ya existe una cuenta con el mismo número y cuyo usuario esté activo
        accountRepository.findByNumberAndUser_ActiveTrue(accountCreate.getNumber())
                .ifPresent(account -> {
                    throw new AlreadyExistsException("Account already exists");
                });

        // Convierte el DTO en una entidad Account utilizando el mapper
        Account account = AccountMapper.toEntity(accountCreate);

        // Guarda la cuenta en la base de datos
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) throws Exception {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getMyOwnAccount() throws Exception {

        // Obtiene el nombre del usuario autenticado desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca al usuario por su username y verifica que esté activo
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        // Verifica que el usuario tenga tarjetas registradas
        // Si no tiene ninguna, se considera inválido continuar
        if (user.getCards().isEmpty()) {
            throw new EmptyCardListException("User has no registered cards.");
        }

        // Obtiene la cuenta del usuario
        Account account = user.getAccounts().getFirst();

        // Construye la respuesta con los datos del usuario y su cuenta
        return AccountResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountNumber(account.getNumber())
                .balance(account.getBalance())
                .build();
    }
}

