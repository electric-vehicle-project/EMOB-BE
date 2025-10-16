/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.DealerDiscountPolicy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealerDiscountPolicyRepository extends JpaRepository<DealerDiscountPolicy, UUID> {
  // Tìm tất cả policy theo danh sách dealer và vehicle
  List<DealerDiscountPolicy> findAllByDealerIdInAndVehicleIdIn(
      List<UUID> dealerIds, List<UUID> vehicleIds);
}
