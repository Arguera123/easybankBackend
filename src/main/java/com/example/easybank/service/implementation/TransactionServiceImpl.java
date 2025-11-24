package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.request.TransactionRequestDTO;
import com.example.easybank.domain.dto.response.PageResponse;
import com.example.easybank.domain.dto.response.TransactionResponseDTO;
import com.example.easybank.domain.mapper.PageMapper;
import com.example.easybank.domain.mapper.TransactionMapper;
import com.example.easybank.exception.*;
import com.example.easybank.repository.BillRepository;
import com.example.easybank.service.TransactionService;
import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.Transaction;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.TransactionRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.util.TransactionSpecifications;
import com.example.easybank.util.generator.RandomCreditCardGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    // Inyecciones de dependencias (Spring las proporciona automáticamente)
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RandomCreditCardGenerator randomCreditCardGenerator;
    private final BillRepository billRepository;

    @Override
    @Transactional  // Garantiza que todas las operaciones se ejecuten en una única transacción de BD
    public void createTransaction(TransactionRequestDTO transactionRequestDTO) throws Exception {

        // Obtiene el username del usuario logueado desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(transactionRequestDTO);

        // Busca al usuario y valida que esté activo
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        // Obtiene la cuenta de origen del usuario autenticado
        Account originAccount = accountRepository
                .findByNumberAndUser_ActiveTrue(user.getAccounts().getFirst().getNumber())
                .orElseThrow(() -> new ModelNotFoundException("Origin account not found"));

        // Busca la cuenta de destino usando el número ingresado por el usuario
        Account destinationAccount = accountRepository
                .findByNumberAndUser_ActiveTrue(transactionRequestDTO.getAccountNumber())
                .orElseThrow(() -> new ModelNotFoundException("Destination account not found"));

        // Verifica que no se esté transfiriendo a la misma cuenta
        if (originAccount.getNumber().equals(destinationAccount.getNumber())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        // Validación del nombre del destinatario
        if (!destinationAccount.getUser().getFirstName().toLowerCase()
                .equals(transactionRequestDTO.getFirstName())) {
            throw new RecipientNameMismatchException("Some recipients do not match");
        }

        // Validación del apellido del destinatario
        if (!destinationAccount.getUser().getLastName().toLowerCase()
                .equals(transactionRequestDTO.getLastName())) {
            throw new RecipientNameMismatchException("Some recipients do not match Last name");
        }

        // Monto a transferir
        BigDecimal amount = transactionRequestDTO.getAmount();

        // Saldo disponible en la cuenta de origen
        BigDecimal originBalance = originAccount.getBalance();

        // Verifica si la cuenta de origen tiene suficiente saldo
        if (originBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in the source account");
        }

        // Actualiza los saldos
        originAccount.setBalance(originBalance.subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Crea la entidad Transaction a partir del DTO
        Transaction transaction = TransactionMapper.toEntity(transactionRequestDTO);
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setType("TRANSFER");

        // Guarda las cuentas con sus nuevos saldos
        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        // Guarda la transacción
        transactionRepository.save(transaction);
    }

    @Override
    public PageResponse<TransactionResponseDTO> getAllTransactions(
            String type,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) throws Exception {

        // Obtiene el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        // Se obtiene la cuenta principal del usuario
        String accountNumber = user.getAccounts().getFirst().getNumber();

        // Construye especificaciones dinámicas (filtros) para la consulta
        Specification<Transaction> spec = Specification.allOf(
                TransactionSpecifications.hasType(type),
                TransactionSpecifications.betweenDates(from, to),
                TransactionSpecifications.hasAccount(accountNumber)
        );

        // Ejecuta la consulta paginada y mapea entidades a DTOs
        Page<TransactionResponseDTO> transactionsPage =
                transactionRepository.findAll(spec, pageable).map(TransactionMapper::toDTO);

        // Convierte la Page de Spring en un PageResponse personalizado
        return PageMapper.map(transactionsPage);
    }
}
