package com.example.easybank.service.implementation;


import com.example.easybank.domain.dto.response.*;
import com.example.easybank.domain.mapper.*;
import com.example.easybank.exception.*;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.RoleRepository;
import com.example.easybank.repository.TransactionRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.service.AdminService;
import com.example.easybank.domain.entity.*;
import com.example.easybank.util.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    // Helper methods
    //------------------------------------
    //Obtiene un usuario activo por ID.
    private UserData getUserEntityById(UUID id) {
        return userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }


    //Obtiene un rol por nombre.
    private Role getRole(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + name));
    }


    //Obtiene una cuenta por ID.
    private Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
    }

    //-------------------------------------------



    //Devuelve todos los usuarios activos
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers() {
        return UserMapper.toDTOList(userRepository.findAllByActiveTrue());
    }


    //Soft delete de un usuario (marca como inactivo).

    @Override
    public void delete(UUID id) {
        UserData user = getUserEntityById(id);
        user.setActive(false); // Soft delete
        userRepository.save(user);
    }


    //Cambia los roles de un usuario.

    @Override
    public void changeRoles(UUID id, List<RoleName> roles) {
        UserData user = getUserEntityById(id);

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }

        Set<Role> newRoles = roles.stream()
                .map(roleEnum -> getRole(roleEnum.name()))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    //Obtiene un usuario por ID
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        UserData user = getUserEntityById(id);

        UserResponseDTO dto = UserMapper.toDTO(user);
        dto.setRoles(user.getRoles().stream().map(Role::getName).toList());
        return dto;
    }


    //Obtiene las cuentas de un usuario activo.
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseAdminDTO> getUserAccounts(UUID userId) {
        UserData user = getUserEntityById(userId);

        return user.getAccounts().stream()
                .map(AccountMapper::toAdminDTO)
                .toList();
    }


    //Obtiene bills de un usuario activo.

    @Override
    @Transactional(readOnly = true)
    public List<BillResponseDTO> getUserBills(UUID userId) {
        UserData user = getUserEntityById(userId);

        return user.getBills().stream()
                .map(BillMapper::toDTO)
                .toList();
    }



    //Obtiene transacciones de un usuario específico o todas si no se pasa ID.
    //Valida UUID y existencia del usuario.
    @Override
    @Transactional(readOnly = true)
    public List<AdminTransactionResponseDTO> getUserTransactions(String id) {

        // Si no se proporciona ID, devolver todas las transacciones
        if (id == null || id.isBlank()) {
            return transactionRepository.findAll()
                    .stream()
                    .map(AdminTransactionMapper::toDTO)
                    .toList();
        }

        // Validación de UUID
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new InvalidUUIDException("Invalid UUID format: " + id);
        }

        // Verifica que el usuario exista y esté activo
        getUserEntityById(userId);

        // Buscar transacciones donde el usuario sea origen o destino
        List<Transaction> transactions =
                transactionRepository
                        .findByOriginAccount_User_IdOrDestinationAccount_User_IdOrderByDateTimeDesc(
                                userId, userId);

        return transactions.stream()
                .map(AdminTransactionMapper::toDTO)
                .toList();
    }


    //Depósito a cuenta de usuario.
    //Valida usuario activo, cuenta válida y monto positivo.

    @Override
    @Transactional
    public void depositToUserAccount(UUID userId,
                                     UUID accountId,
                                     BigDecimal amount,
                                     String description,
                                     String performedByUsername) {

        // Obtener admin que realiza la acción
        UserData admin = userRepository.findByUsernameAndActiveTrue(performedByUsername)
                .orElseThrow(() -> new UserNotFoundException(
                        "Admin user not found with name: " + performedByUsername));

        // Obtener usuario destino y cuenta
        UserData user = getUserEntityById(userId);
        Account account = getAccountById(accountId);

        // Verificar que la cuenta pertenezca al usuario
        if (!account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to the specified user");
        }

        // Verificar que admin tenga al menos una cuenta
        if (admin.getAccounts().isEmpty()) {
            throw new IllegalArgumentException("Admin does not have an account to use as origin");
        }

        // Validar cantidad a depositar
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be a positive number");
        }

        Account adminAccount = admin.getAccounts().getFirst();

        // Crear transacción y guardarla
        Transaction depositTx = Transaction.builder()
                .originAccount(adminAccount)
                .destinationAccount(account)
                .amount(amount)
                .type("DEPOSIT")
                .description(description)
                .dateTime(LocalDateTime.now())
                .build();

        transactionRepository.save(depositTx);

        // Actualizar balance de la cuenta destino
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}
