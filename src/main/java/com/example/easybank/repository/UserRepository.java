package com.example.easybank.repository;

import com.example.easybank.domain.entity.UserData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends GenericRepository<UserData, UUID> {
    //Optional<UserData> findByUsernameOrEmail(String username, String email);
    //Optional<UserData> findByEmail(String email);
    //Optional<UserData> findByUsername(String username);
    //Optional<UserData> findByDui(String dui);

    //revisa usuario activo
    Optional<UserData> findByIdAndActiveTrue(UUID id);
    Optional<UserData> findByUsernameOrEmailAndActiveTrue(String username, String email);
    Optional<UserData> findByEmailAndActiveTrue(String email);
    Optional<UserData> findByUsernameAndActiveTrue(String username);
    Optional<UserData> findByDuiAndActiveTrue(String dui);
    List<UserData> findAllByActiveTrue();




}
