/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.entity.Customer;
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
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  Page<Customer> findAllByDealer(Dealer dealer, Pageable pageable);

  @Query("""
    SELECT c
    FROM Customer c
    WHERE (:keyword IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (
        :statuses IS NULL 
        OR c.status IN :statuses
      )
""")
  Page<Customer> searchAndFilter(
          @Param("keyword") String keyword,
          @Param("statuses") List<CustomerStatus> statuses,
          Pageable pageable);
}
