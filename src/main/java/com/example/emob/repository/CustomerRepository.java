/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  Page<Customer> findAllByDealer(Dealer dealer, Pageable pageable);
}
