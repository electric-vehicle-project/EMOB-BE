/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.VehicleRequestStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.VehicleRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRequestRepository extends JpaRepository<VehicleRequest, UUID> {
  Page<VehicleRequest> findAllByIsDeletedFalseAndDealer(Dealer dealer, Pageable pageable);

  @Query(
      """
    SELECT vr
    FROM VehicleRequest vr
    JOIN vr.dealer d
    WHERE vr.isDeleted = false
      AND d = :dealer
      AND (
        :keyword IS NULL
        OR LOWER(CAST(vr.totalQuantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      AND (
        :statuses IS NULL
        OR vr.status IN :statuses
      )
""")
  Page<VehicleRequest> searchAndFilter(
      @Param("dealer") Dealer dealer,
      @Param("keyword") String keyword,
      @Param("statuses") List<VehicleRequestStatus> statuses,
      Pageable pageable);
}
