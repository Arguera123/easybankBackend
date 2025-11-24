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

        // Verifica si el nombre de usuario ya existe y está activo
        userRepository.findByUsernameAndActiveTrue(registerDTO.getUsername())
                .ifPresent(user -> {
                    throw new AlreadyExistsException("User already exists");
                });

        // Verifica si el email ya está registrado en la base de datos
        userRepository.findByEmailAndActiveTrue(registerDTO.getEmail())
                .ifPresent(user -> {
                    throw new AlreadyExistsException("User already exists");
                });

        // Verifica si el número de DUI ya está registrado en el sistema
        userRepository.findByDuiAndActiveTrue(registerDTO.getDui())
                .ifPresent(user -> {
                    throw new AlreadyExistsException("User already exists");
                });

        // Convierte el DTO de registro a una entidad UserData
        UserData user = UserMapper.toEntity(registerDTO);

        // Obtiene el rol "USER" para asignárselo al nuevo usuario
        Optional<Role> role = roleRepository.findByName("USER");
        Role userRole = role.orElseThrow(() -> new EntityNotFoundException("Role not found"));

        // Asigna el rol al usuario
        user.setRoles(Set.of(userRole));

        // Guarda el usuario en la base de datos
        UserData userSaved = userRepository.save(user);

        // Crea una cuenta bancaria inicial para el nuevo usuario
        AccountCreateDTO accountCreateDTO = AccountCreateDTO.builder()
                .number(AccountNumberGenerator.generateAccountNumber()) // Genera número de cuenta
                .balance(new BigDecimal("50.00")) // Saldo inicial
                .type("STANDARD") // Tipo de cuenta
                .currency("USD") // Moneda
                .userData(userSaved) // Relaciona la cuenta con el usuario guardado
                .build();

        // Guarda la cuenta del usuario
        accountServiceImpl.save(accountCreateDTO);
    }

    @Override
    public TokenResponse login(LoginDTO loginDTO) throws Exception {
        try {
            // Autentica al usuario usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword()
                    )
            );

            // Establece la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Genera un token JWT asociado a la autenticación
            String token = jwtTokenProvider.generateToken(authentication);

            // Devuelve el token al cliente
            return TokenResponse.builder()
                    .token(token)
                    .build();

        } catch (BadCredentialsException e) {
            // Lanzado cuando el usuario o la contraseña son incorrectos
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Override
    public UserResponseDTO whoami() throws Exception {

        // Obtiene el username del usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca al usuario en la base de datos
        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Marca al usuario como "activo" solo si tiene tarjetas asociadas
        user.setActive(!user.getCards().isEmpty());

        // Convierte la entidad UserData a un DTO para la respuesta
        UserResponseDTO whoamiResponse = UserMapper.toDTO(user);

        // Convierte la lista de roles a una lista de strings
        whoamiResponse.setRoles(
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );

        // Retorna los datos del usuario autenticado
        return whoamiResponse;
    }



}