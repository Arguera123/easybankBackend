package com.example.easybank.controller;

import com.example.easybank.domain.dto.request.TransactionRequestDTO;
import com.example.easybank.domain.dto.response.PageResponse;
import com.example.easybank.domain.dto.response.TransactionResponseDTO;
import com.example.easybank.service.TransactionService;
import com.example.easybank.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.easybank.util.Constant.*;

@RequestMapping(API + TRANSACTION)
@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(CREATE)
    public ResponseEntity<GenericResponse> create(@RequestBody TransactionRequestDTO transaction) throws Exception {
        transactionService.createTransaction(transaction);
        return GenericResponse.builder()
                .message("Transaction successfully")
                .status(HttpStatus.CREATED)
                .build().buildResponse();
    }

    @GetMapping(FIND)
    public ResponseEntity<GenericResponse> findTransaction(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        Pageable pageable
    ) throws Exception {
        PageResponse<TransactionResponseDTO> response = transactionService.getAllTransactions(
                type, from, to, pageable
        );
        return GenericResponse.builder()
                .data(response.getContent())
                .message("Transactions found")
                .status(HttpStatus.OK)
                .pageNumber(response.getPageNumber())
                .pageSize(response.getPageSize())
                .first(response.isFirst())
                .last(response.isLast())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .build().buildResponse();
    }
}
