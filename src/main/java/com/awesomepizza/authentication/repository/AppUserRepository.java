package com.awesomepizza.authentication.repository;

import com.awesomepizza.authentication.entity.AppUserDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserDB, Long> {
    Optional<AppUserDB> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}



