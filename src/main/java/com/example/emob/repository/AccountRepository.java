/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.Role;
import com.example.emob.entity.Account;

import java.util.List;
import java.util.UUID;

import com.example.emob.entity.Dealer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Account findAccountById(UUID id);

  Account findAccountByEmail(String email);

  Page<Account> findByRoleAndDealer(Role role, Dealer dealer, Pageable pageable);
  Page<Account> findByRoleIn(List<Role> roles, Pageable pageable);


}
