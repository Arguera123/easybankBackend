package com.example.easybank.controller;

import com.example.easybank.service.implementation.CardServiceImpl;
import com.example.easybank.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.easybank.util.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + CARD)
public class CardController {
    private final CardServiceImpl cardService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(CREATE)
    public ResponseEntity<GenericResponse> createCard() throws Exception {
        cardService.create();
        return GenericResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Card created successfully")
                .build().buildResponse();
    }
}
