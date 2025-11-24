package com.example.easybank.domain.mapper;

import com.example.easybank.domain.dto.request.RegisterDTO;
import com.example.easybank.domain.dto.response.UserResponseDTO;
import com.example.easybank.domain.entity.Role;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.util.PasswordUtil;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserData toEntity(RegisterDTO registerDTO) {
        return UserData.builder()
                .username(registerDTO.getUsername())
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .dui(registerDTO.getDui())
                .email(registerDTO.getEmail())
                .hashedPassword(PasswordUtil.hashPassword(registerDTO.getPassword()))
                .active(true)
                .build();
    }

    public static UserResponseDTO toDTO(UserData userData) {
        return UserResponseDTO.builder()
                .id(userData.getId())
                .dui(userData.getDui())
                .username(userData.getUsername())
                .name(userData.getFirstName() + " " + userData.getLastName())
                .email(userData.getEmail())
                .roles(userData.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .active(userData.getActive())
                .build();
    }

    public static List<UserResponseDTO> toDTOList(List<UserData> userData) {
        return userData.stream()
                .map(UserMapper::toDTO)
                .toList();
    }



}
