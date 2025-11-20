/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.AccountStatus;
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

  @Query("""
  SELECT a FROM Account a
  WHERE 
    (:role IS NULL OR a.role = :role)
    AND (:dealer IS NULL OR a.dealer = :dealer)
    AND (:statuses IS NULL OR a.status IN :statuses)
    AND (
      :keyword IS NULL
      OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
  Page<Account> findByRoleAndDealer(
          @Param("role") Role role,
          @Param("dealer") Dealer dealer,
          @Param("statuses") List<AccountStatus> statuses,
          @Param("keyword") String keyword,
          Pageable pageable
  );

  List<Account> findByRoleAndDealer(Role role, Dealer dealer);

  @Query("""
  SELECT a FROM Account a
  WHERE 
    (:roles IS NULL OR a.role IN :roles)
    AND (:statuses IS NULL OR a.status IN :statuses)
    AND (
      :keyword IS NULL
      OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
  Page<Account> findByRolesStatusesAndKeyword(
          @Param("roles") List<Role> roles,
          @Param("statuses") List<AccountStatus> statuses,
          @Param("keyword") String keyword,
          Pageable pageable
  );
}
