package com.securevault.passwordservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_history")
@Data
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password_entry_id", nullable = false)
    private Long passwordEntryId;

    @Column(name = "encrypted_password", nullable = false, length = 500)
    private String encryptedPassword;


}