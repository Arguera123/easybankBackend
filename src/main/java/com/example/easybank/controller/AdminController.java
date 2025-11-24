package com.example.easybank.controller;


import com.example.easybank.domain.dto.request.ChangeRoleRequestDTO;
import com.example.easybank.domain.dto.request.DepositRequestDTO;
import com.example.easybank.domain.dto.response.*;
import com.example.easybank.service.AdminService;
import com.example.easybank.util.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<GenericResponse> getAllUsers() {

        List<UserResponseDTO> users = adminService.findAllUsers();

        return GenericResponse.success("Users found", users);
    }


    @GetMapping(USER_LIST + ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserById(@PathVariable UUID id) {

        UserResponseDTO user = adminService.getUserById(id);

        return GenericResponse.success("User found", user);
    }



    @DeleteMapping(USER_LIST + DELETE + ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable UUID id) {

        adminService.delete(id);

        return GenericResponse.accepted("Successfully deleted user");

    }


    @PutMapping(USER_LIST + CHANGE_ROLE + ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> changeUserRoles(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRoleRequestDTO request

    ) {

        adminService.changeRoles(id, request.getRoles());

        return GenericResponse.success("Roles updated successfully");
    }


    @GetMapping(USER_LIST + ID + ACCOUNT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserAccounts(@PathVariable UUID id) {

        return GenericResponse.success(
                "User's accounts found",
                adminService.getUserAccounts(id)
        );
    }


    @GetMapping(USER_LIST + ID + BILL)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getUserBills(@PathVariable UUID id) {

        return GenericResponse.success(
                "User's bills found",
                adminService.getUserBills(id)
        );
    }


    @PostMapping(USER_LIST + ID + DEPOSIT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> depositToUserAccount(
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequestDTO request,
            Authentication authentication
    ) {

        adminService.depositToUserAccount(
                id,
                request.getAccountId(),
                request.getAmount(),
                request.getDescription(),
                authentication.getName()
        );

        return GenericResponse.success("Deposit completed successfully");
    }



    @GetMapping(TRANSACTION)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse> getTransactions(
            @RequestParam(required = false) String id
    ) {

        return GenericResponse.success(
                "Transactions retrieved",
                adminService.getUserTransactions(id)
        );
    }

}
