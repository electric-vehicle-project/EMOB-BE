/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.Role;
import com.example.emob.entity.Account;
import com.example.emob.entity.Dealer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Account findAccountById(UUID id);

  Account findAccountByEmail(String email);

  Page<Account> findByRoleAndDealer(Role role, Dealer dealer, Pageable pageable);

  List<Account> findByRoleAndDealer(Role role, Dealer dealer);

  @Query("""
  SELECT a FROM Account a
  WHERE (:roles IS NULL OR a.role IN :roles)
""")
  Page<Account> findByRolesOptional(
          @Param("roles") List<Role> roles,
          Pageable pageable
  );
}
