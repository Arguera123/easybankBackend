package com.example.easybank.controller;

import com.example.easybank.domain.dto.request.LoginDTO;
import com.example.easybank.domain.dto.request.RegisterDTO;
import com.example.easybank.domain.dto.response.TokenResponse;
import com.example.easybank.domain.dto.response.UserResponseDTO;
import com.example.easybank.service.implementation.AuthServiceImpl;
import com.example.easybank.util.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.example.easybank.util.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API + AUTH)
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping(REGISTER)
    public ResponseEntity<GenericResponse> register(@RequestBody @Valid RegisterDTO userToRegister) throws Exception {
        authService.register(userToRegister);
        return GenericResponse.builder()
                .status(HttpStatus.CREATED)
                .message("User registered successfully")
                .build().buildResponse();
    }

    @PostMapping(LOGIN)
    public ResponseEntity<GenericResponse> login(@RequestBody @Valid LoginDTO userToLogin) throws Exception {
        TokenResponse tokenResponse = authService.login(userToLogin);
        return GenericResponse.builder()
                .status(HttpStatus.OK)
                .data(tokenResponse)
                .message("User login correctly")
                .build().buildResponse();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(WHO_AM_I)
    public ResponseEntity<GenericResponse> whoAmI() throws Exception {
        UserResponseDTO userData = authService.whoami();
        return GenericResponse.builder()
                .status(HttpStatus.OK)
                .data(userData)
                .message("WhoAmI correctly")
                .build().buildResponse();
    }
}
