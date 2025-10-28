/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.DiscountPolicyStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.DealerDiscountPolicy;
import com.example.emob.entity.ElectricVehicle;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealerDiscountPolicyRepository extends JpaRepository<DealerDiscountPolicy, UUID> {
  // Tìm tất cả policy theo danh sách dealer và vehicle
  List<DealerDiscountPolicy> findAllByDealerIdInAndVehicleIdIn(
      List<UUID> dealerIds, List<UUID> vehicleIds);

  DealerDiscountPolicy findByDealerAndVehicleAndStatus(
      Dealer dealer, ElectricVehicle vehicle, DiscountPolicyStatus status);

  List<DealerDiscountPolicy> findAllByStatusAndExpiryDateBefore(
      DiscountPolicyStatus status, LocalDate date);

  List<DealerDiscountPolicy> findAllByStatusAndEffectiveDateBefore(
      DiscountPolicyStatus status, LocalDate date);

  @Query("""
    SELECT d
    FROM DealerDiscountPolicy d
    WHERE 
      (
        :keyword IS NULL 
        OR CAST(d.effectiveDate AS string) LIKE CONCAT('%', :keyword, '%')
      )
      AND (
        :status IS NULL 
        OR d.status = :status
      )
    """)
  Page<DealerDiscountPolicy> searchAndFilter(
          @Param("keyword") String keyword,
          @Param("status") DiscountPolicyStatus status,
          Pageable pageable);
}
