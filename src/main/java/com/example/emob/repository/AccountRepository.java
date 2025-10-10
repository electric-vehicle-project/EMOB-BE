/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Account;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Account findAccountById(UUID id);

  Account findAccountByEmail(String email);
}
