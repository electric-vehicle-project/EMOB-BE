package com.example.emob.repository;

import com.example.emob.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findAccountById(UUID id);
    Account findAccountByEmail(String email);
}

