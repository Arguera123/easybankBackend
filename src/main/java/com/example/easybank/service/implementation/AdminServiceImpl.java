package com.example.easybank.service.implementation;


import com.example.easybank.domain.dto.response.*;
import com.example.easybank.domain.mapper.*;
import com.example.easybank.exception.*;
import com.example.easybank.repository.*;
import com.example.easybank.service.AdminService;
import com.example.easybank.domain.entity.*;
import com.example.easybank.util.RoleName;
import com.example.easybank.util.TransactionSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final BillRepository billRepository;


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
    public PageResponse<UserResponseDTO> findAllUsers(Pageable pageable) {

        Page<UserResponseDTO> users = userRepository.findAll(pageable).map(UserMapper::toDTO);

        return PageMapper.map(users);
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
    @Transactional//(readOnly = true)
    public PageResponse<BillResponseDTO> getUserBills(UUID userId, Pageable pageable) {
        UserData user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<BillResponseDTO> bills = billRepository.getBillsByUser(user, pageable).map(BillMapper::toDTO);

        return PageMapper.map(bills);
    }

    @Override
    @Transactional//(readOnly = true)
    public PageResponse<AdminTransactionResponseDTO> getUserTransactions(UUID userId, Pageable pageable) {

        UserData user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Specification<Transaction> spec = Specification.allOf(
                TransactionSpecifications.hasAccount(user.getAccounts().getFirst().getNumber())
        );

        Page<AdminTransactionResponseDTO> transactions =
            transactionRepository.findAll(spec, pageable).map(UserTransactionMapper::toDTO);

        return PageMapper.map(transactions);
    }

    @Override
    public PageResponse<AdminTransactionResponseDTO> findAll(Pageable pageable) throws Exception{
        Page<AdminTransactionResponseDTO> transactions = transactionRepository.findAll(pageable).map(UserTransactionMapper::toDTO);

        return PageMapper.map(transactions);
    }


    //Depósito a cuenta de usuario.
    //Valida usuario activo, cuenta válida y monto positivo.
    @Override
    @Transactional
    public void depositToUserAccount(
            UUID userId,
            UUID accountId,
            BigDecimal amount,
            String description
    ) {

        // Obtener admin que realiza la acción
        String userAdmin = SecurityContextHolder.getContext().getAuthentication().getName();
        UserData admin = userRepository.findByUsernameAndActiveTrue(userAdmin)
                .orElseThrow(() -> new UserNotFoundException(
                        "Admin user not found with name: " + userAdmin));

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
