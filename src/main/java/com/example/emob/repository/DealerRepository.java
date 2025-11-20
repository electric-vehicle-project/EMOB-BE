/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.Region;
import com.example.emob.entity.Dealer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.emob.model.response.MonthlyDealerRevenueResponse;
import com.example.emob.model.response.MonthlyRevenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {
  Page<Dealer> findAllByIsDeletedFalse(Pageable pageable);

  @Query(
      """
        SELECT d
        FROM Dealer d
        WHERE d.isDeleted = false
          AND (:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(d.emailContact) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(d.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(d.phoneContact) LIKE LOWER(CONCAT('%', :keyword, '%')))

          AND (:country IS NULL OR LOWER(d.country) = LOWER(:country))
            AND (:regions IS NULL OR d.region IN :regions)
        """)
  Page<Dealer> searchAndFilter(
      @Param("keyword") String keyword, @Param("country") String country,@Param("regions") List<Region> regions, Pageable pageable);

  @Query(
          value = """
    WITH months AS (
        SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
        SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL
        SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL
        SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    )
    SELECT
        m.month,
        COALESCE(SUM(c.total_price), 0) AS totalRevenue,
        COALESCE(COUNT(c.id), 0) AS totalContracts,
        COALESCE(SUM(so.total_quantity), 0) AS totalVehiclesSold
    FROM months m
    LEFT JOIN sale_contract c
        ON MONTH(c.sign_date) = m.month
        AND YEAR(c.sign_date) = :year
        AND c.status = 'SIGNED'
    INNER JOIN sale_order so ON c.sale_order = so.id
    INNER JOIN vehicle_request vr ON so.id = vr.sale_order_id
    LEFT JOIN dealer d ON vr.dealer_id = d.id
    WHERE (:region IS NULL OR d.region = :region)
      AND (:country IS NULL OR d.country = :country)
    GROUP BY m.month
    ORDER BY m.month
    """,
          nativeQuery = true
  )
  List<MonthlyDealerRevenueResponse> getDealerRevenue12MonthsFiltered(
          @Param("year") Integer year,
          @Param("region") Region region,
          @Param("country") String country
  );


  @Query(
          value = """
    WITH months AS (
        SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
        SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL
        SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL
        SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    )
    SELECT
        m.month,
        COALESCE(SUM(c.total_price), 0) AS totalRevenue,
        COALESCE(COUNT(c.id), 0) AS totalContracts,
        COALESCE(SUM(so.total_quantity), 0) AS totalVehiclesSold
    FROM months m
    LEFT JOIN sale_contract c
        ON MONTH(c.sign_date) = m.month
        AND YEAR(c.sign_date) = :year
        AND c.status = 'SIGNED'
    INNER JOIN sale_order so ON c.sale_order = so.id           -- chỉ lấy saleContract có SaleOrder
    INNER JOIN quotation q ON so.id = q.sale_order_id           -- SaleOrder phải có Quotation
    INNER JOIN dealer d ON q.dealer_id = d.id  -- Dealer trùng giữa Quotation & SaleOrder
    WHERE d.id = :dealerId                    -- ✅ chỉ lấy dealer truyền vào
    GROUP BY m.month
    ORDER BY m.month
    """,
          nativeQuery = true
  )
  List<MonthlyDealerRevenueResponse> getDealerRevenue12MonthsFilteredOfDealer(
          @Param("year") Integer year,
          @Param("dealerId") UUID dealerId
  );


}
