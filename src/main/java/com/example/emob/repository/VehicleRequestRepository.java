/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.VehicleRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRequestRepository extends JpaRepository<VehicleRequest, UUID> {
  Page<VehicleRequest> findAllByIsDeletedFalseAndDealer(Dealer dealer, Pageable pageable);
}
