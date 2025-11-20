/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.VehicleRequestStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.VehicleRequest;
import java.time.LocalDateTime;
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
  @Query(
      """
    SELECT vr
    FROM VehicleRequest vr
    JOIN vr.dealer d
    WHERE vr.isDeleted = false
      AND d = :dealer
      AND (
        :keyword IS NULL
              OR LOWER(str(vr.totalQuantity)) LIKE LOWER(CONCAT('%', :keyword, '%'))
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

  @Query(
          """
          SELECT ev.model,
                 vri.color,
                 FUNCTION('YEAR', vr.createdAt) AS year,
                 FUNCTION('MONTH', vr.createdAt) AS month,
                 SUM(vri.quantity)
          FROM Dealer d
          INNER JOIN d.vehicleRequests vr
          INNER JOIN vr.vehicleRequestItems vri
          INNER JOIN vri.vehicle ev
          INNER JOIN vr.saleOrder so
          INNER JOIN so.contract c
          WHERE c.status = 'SIGNED'
            AND vri.vehicleStatus = 'NORMAL'
            AND vr.isDeleted= false
            AND ev.isDeleted= false 
            AND ev.model = :model
            AND vr.createdAt >= :threeMonthsAgo
          GROUP BY ev.model,
                   vri.color,
                   FUNCTION('YEAR', vr.createdAt),
                   FUNCTION('MONTH', vr.createdAt)
          ORDER BY ev.model,
                   FUNCTION('YEAR', vr.createdAt),
                   FUNCTION('MONTH', vr.createdAt)
          """
  )
  List<Object[]> findSignedRequestsRaw(@Param("threeMonthsAgo") LocalDateTime threeMonthsAgo, @Param("model") String model);

  @Query("""
    SELECT vr
    FROM VehicleRequest vr
    JOIN vr.dealer d
    WHERE vr.isDeleted = false
      AND (
        :keyword IS NULL
        OR LOWER(CAST(vr.totalQuantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      AND (
        :statuses IS NULL
        OR vr.status IN :statuses
      )
""")
  Page<VehicleRequest> searchAndFilterByAdmin(
          @Param("keyword") String keyword,
          @Param("statuses") List<VehicleRequestStatus> statuses,
          Pageable pageable
  );
}
