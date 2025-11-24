package com.example.easybank.domain.dto.request;

import com.example.easybank.util.RoleName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeRoleRequestDTO {

    @NotNull(message = "Roles list cannot be null")
    @NotEmpty(message = "User must have at least one role")
    private List<RoleName> roles;
}
