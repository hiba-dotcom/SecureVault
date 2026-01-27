package com.securevault.passwordservice.repository;

import com.securevault.passwordservice.model.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {

    List<PasswordEntry> findByUserId(String userId);

    Optional<PasswordEntry> findByIdAndUserId(Long id, String userId);

    List<PasswordEntry> findByUserIdAndWebsiteContainingIgnoreCase(String userId, String website);

    void deleteByIdAndUserId(Long id, String userId);

    boolean existsByUserIdAndWebsiteAndUsername(String userId, String website, String username);

    boolean existsByIdAndUserId(Long id, String username);
}