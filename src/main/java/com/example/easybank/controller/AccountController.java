package com.example.easybank.controller;

import com.example.easybank.domain.dto.response.AccountResponseDTO;
import com.example.easybank.service.AccountService;
import com.example.easybank.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.easybank.util.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + ACCOUNT )
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping(FIND_OWN)
    public ResponseEntity<GenericResponse> findOwnAccount() throws Exception {
         AccountResponseDTO accountResponseDTO = accountService.getMyOwnAccount();

         return GenericResponse.builder()
                 .status(HttpStatus.OK)
                 .data(accountResponseDTO)
                 .message("Account found")
                 .build().buildResponse();
    }
}
