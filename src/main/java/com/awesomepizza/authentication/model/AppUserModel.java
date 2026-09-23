package com.awesomepizza.authentication.model;

import com.awesomepizza.authentication.enumeration.UserRole;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class AppUserModel {
    Long id;
    String username;
    String passwordHash;
    UserRole role;
    boolean enabled;
    Instant createdAt;
    Instant updatedAt;
}



