package com.example.easybank.service;

import com.example.easybank.domain.dto.request.LoginDTO;
import com.example.easybank.domain.dto.request.RegisterDTO;
import com.example.easybank.domain.dto.response.TokenResponse;
import com.example.easybank.domain.dto.response.UserResponseDTO;

public interface AuthService {
    public void register(RegisterDTO registerDTO) throws Exception;
    public TokenResponse login(LoginDTO loginDTO) throws Exception;
    public UserResponseDTO whoami() throws Exception;
}
