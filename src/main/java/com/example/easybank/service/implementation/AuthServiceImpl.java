package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.AccountCreateDTO;
import com.example.easybank.domain.dto.request.LoginDTO;
import com.example.easybank.domain.dto.request.RegisterDTO;
import com.example.easybank.domain.dto.response.TokenResponse;
import com.example.easybank.domain.dto.response.UserResponseDTO;
import com.example.easybank.domain.mapper.UserMapper;
import com.example.easybank.exception.*;
import com.example.easybank.service.AccountService;
import com.example.easybank.service.AuthService;
import com.example.easybank.domain.entity.Role;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.repository.RoleRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.security.JwtTokenProvider;
import com.example.easybank.util.AccountNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountServiceImpl;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) throws Exception {

        // Verificar si el nombre de usuario ya esta tomado
        userRepository.findByUsernameAndActiveTrue(registerDTO.getUsername())
            .ifPresent(user -> {
                throw new AlreadyExistsException("User already exists");
            });

        // Verificar si el email ya exisste en la base de datos
        userRepository.findByEmailAndActiveTrue(registerDTO.getEmail())
            .ifPresent(user -> {
                throw new AlreadyExistsException("User already exists");
            });

        // Verificar si el DUi ya existe en la base de datos
        userRepository.findByDuiAndActiveTrue(registerDTO.getDui())
            .ifPresent(user -> {
                throw new AlreadyExistsException("User already exists");
            });

        UserData user = UserMapper.toEntity(registerDTO);

        Optional<Role> role = roleRepository.findByName("USER");
        Role userRole = role.orElseThrow(() -> new EntityNotFoundException("Role not found"));
        user.setRoles(Set.of(userRole));

        UserData userSaved = userRepository.save(user);

        AccountCreateDTO accountCreateDTO = AccountCreateDTO.builder()
            .number(AccountNumberGenerator.generateAccountNumber())
            .balance(new BigDecimal("50.00"))
            .type("STANDARD")
            .currency("USD")
            .userData(userSaved)
            .build();

        accountServiceImpl.save(accountCreateDTO);
    }

    @Override
    public TokenResponse login(LoginDTO loginDTO) throws Exception {
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);

            return TokenResponse.builder()
                .token(token)
                .build();

        } catch (BadCredentialsException e){
            throw new InvalidCredentialsException("Invalid username or password");
        }

    }

    @Override
    public UserResponseDTO whoami() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setActive(!user.getCards().isEmpty());
        UserResponseDTO whoamiResponse = UserMapper.toDTO(user);

        whoamiResponse.setRoles(user.getRoles().stream().map(Role::getName).toList());

        return whoamiResponse;
    }
}
