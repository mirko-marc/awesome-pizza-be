package com.awesomepizza.authentication.entity;

import com.awesomepizza.authentication.enumeration.UserRole;
import com.awesomepizza.shared.entity.TechnicalDataDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppUserDB extends TechnicalDataDB {

    @Id
    @SequenceGenerator(name = "app_user_id_generator", sequenceName = "app_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_generator")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    public static AppUserDB create() {
        return new AppUserDB();
    }
}



