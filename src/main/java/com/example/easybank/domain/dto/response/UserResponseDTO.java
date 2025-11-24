package com.example.easybank.domain.dto.response;

import com.example.easybank.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String name;
    private String email;
    private String dui;
    private Boolean active;
    private List<String> roles;
}
