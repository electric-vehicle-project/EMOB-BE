package com.example.emob.repository;

import com.example.emob.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountById(long id);
    Account findAccountByEmail(String email);
}

