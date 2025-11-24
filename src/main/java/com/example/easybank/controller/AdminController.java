package com.example.easybank.controller;


import com.example.easybank.domain.dto.request.ChangeRoleRequestDTO;
import com.example.easybank.domain.dto.request.DepositRequestDTO;
import com.example.easybank.domain.dto.response.*;
import com.example.easybank.service.AdminService;
import com.example.easybank.util.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.easybank.util.Constant.*;

@RequestMapping(API + ADMIN)
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping(USER_LIST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getAllUsers(Pageable pageable) throws Exception {
        PageResponse<UserResponseDTO> page = adminService.findAllUsers(pageable);

        return GenericResponse.builder()
                .data(page.getContent())
                .message("Users found")
                .status(HttpStatus.OK)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .first(page.isFirst())
                .last(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build().buildResponse();
    }

    @GetMapping(USER_LIST + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserById(@PathVariable("id")  UUID id) throws Exception {
        UserResponseDTO user = adminService.getUserById(id);

        return GenericResponse.success("User found", user);
    }


    @DeleteMapping(USER_LIST + DELETE + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable("id") UUID id) throws Exception {
        adminService.delete(id);

        return GenericResponse.accepted("Successfully deleted user");

    }


    @PutMapping(USER_LIST + CHANGE_ROLE + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> changeUserRoles(@PathVariable ("id") UUID id, @RequestBody ChangeRoleRequestDTO request) {

        adminService.changeRoles(id, request.getRoles());

        return GenericResponse.success("Roles updated successfully");
    }

    @GetMapping(USER_LIST + "/{id}/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserAccounts(@PathVariable ("id") UUID id) {
        List<AccountResponseAdminDTO> accounts = adminService.getUserAccounts(id);
        return GenericResponse.builder()
                .data(accounts)
                .message("User's accounts found")
                .status(HttpStatus.OK)
                .build().buildResponse();
    }

    @GetMapping(USER_LIST + "/{id}/bills")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserBills(
            @PathVariable ("id") UUID id,
            Pageable pageable
    ) throws Exception {
        PageResponse<BillResponseDTO> page = adminService.getUserBills(id, pageable);
        return GenericResponse.builder()
                .data(page.getContent())
                .message("User's bills found")
                .status(HttpStatus.OK)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .first(page.isFirst())
                .last(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build().buildResponse();
    }

    @GetMapping(TRANSACTION + "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserTransactions(
            @PathVariable ("id") UUID id,
            Pageable pageable
    ) throws Exception {

        PageResponse<AdminTransactionResponseDTO> page = adminService.getUserTransactions(id, pageable);

        return GenericResponse.builder()
                .data(page.getContent())
                .message("User's transactions found")
                .status(HttpStatus.OK)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .first(page.isFirst())
                .last(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build().buildResponse();
    }


    @PostMapping(USER_LIST + "/{id}/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> depositToUserAccount(
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequestDTO request
    ) {
        adminService.depositToUserAccount(
                id,
                request.getAccountId(),
                request.getAmount(),
                request.getDescription()
        );

        return GenericResponse.success("Deposit completed successfully");
    }

    @GetMapping(TRANSACTION)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> findAllTransactions(Pageable pageable) throws Exception {
        PageResponse<AdminTransactionResponseDTO> page = adminService.findAll(pageable);

        return GenericResponse.builder()
                .status(HttpStatus.OK)
                .message("All transactions found")
                .data(page.getContent())
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .first(page.isFirst())
                .last(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build().buildResponse();
    }

//    @GetMapping(TRANSACTION)
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<GenericResponse> getTransactions(
//            @RequestParam(required = false) UUID id,
//            Pageable pageable
//    ) {
//
//        return GenericResponse.success(
//                "Transactions retrieved",
//                adminService.getUserTransactions(id, pageable)
//        );
//    }
}
