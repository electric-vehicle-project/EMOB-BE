/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Customer;
import com.example.emob.model.response.CustomerRevenueItemResponse;
import com.example.emob.model.response.DealerRevenueItemResponse;
import com.example.emob.entity.Dealer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        """)
  Page<Dealer> searchAndFilter(
          @Param("keyword") String keyword, @Param("country") String country, Pageable pageable);


  @Query(value = """
    SELECT 
      BIN_TO_UUID(vr.dealer_id) AS dealerId,
      SUM(c.total_price) AS totalRevenue,
      COUNT(c.id) AS totalContracts,
      SUM(so.total_quantity) AS totalVehiclesSold,
      MONTH(c.sign_date) AS month,
      YEAR(c.sign_date) AS year
    FROM sale_contract c
    JOIN sale_order so ON c.sale_order = so.id
    JOIN vehicle_request vr ON so.id = vr.sale_order_id
    WHERE c.status = 'SIGNED'
      AND (:month IS NULL OR MONTH(c.sign_date) = :month)
    GROUP BY vr.dealer_id, YEAR(c.sign_date), MONTH(c.sign_date)
    ORDER BY YEAR(c.sign_date), MONTH(c.sign_date)
""",
          nativeQuery = true)
  Page<DealerRevenueItemResponse> getDealerRevenueReportByMonth( @Param("month") Integer month, Pageable pageable);


  @Query(value = """
  SELECT 
      BIN_TO_UUID(vr.dealer_id) AS dealerId,
      SUM(c.total_price) as totalRevenue,
      COUNT(c.id) as totalContracts,
      SUM(so.total_quantity) as totalVehiclesSold,
      NULL AS month,
      NULL AS year
  FROM sale_contract c
  JOIN sale_order so ON c.sale_order = so.id
  JOIN vehicle_request vr ON so.id = vr.sale_order_id
  WHERE c.status = 'SIGNED'
    AND vr.dealer_id = :dealerId
  GROUP BY vr.dealer_id
  """,
          nativeQuery = true)
  Optional<DealerRevenueItemResponse> getDealerRevenueById(@Param("dealerId") UUID dealerId);


//  // Lấy danh sách khách hàng của dealer
@Query(value = """
    SELECT
        BIN_TO_UUID(cus.id) AS customerId,
        SUM(c.total_price) AS totalRevenue,
        COUNT(c.id) AS totalContracts,
        SUM(so.total_quantity) AS totalVehiclesPurchased,
        MONTH(c.sign_date) AS month,
        YEAR(c.sign_date) AS year
    FROM sale_contract c
    JOIN sale_order so ON c.sale_order = so.id
    JOIN quotation q ON q.sale_order_id = so.id
    JOIN customer cus ON q.customer_id = cus.id
    JOIN dealer d ON cus.dealer_id = d.id
    WHERE c.status = 'SIGNED'
      AND d.id = UUID_TO_BIN(:dealerId)
      AND (:month IS NULL OR MONTH(c.sign_date) = :month)
    GROUP BY cus.id, YEAR(c.sign_date), MONTH(c.sign_date)
    ORDER BY YEAR(c.sign_date), MONTH(c.sign_date)
    """, nativeQuery = true)
Page<CustomerRevenueItemResponse> getCustomerRevenueReport(
        @Param("dealerId") String dealerId, // THÊM THAM SỐ LỌC THEO DEALER
        @Param("month") Integer month,
        Pageable pageable);

  @Query(value = """
SELECT
    BIN_TO_UUID(cus.id) AS customerId,
    SUM(c.total_price) AS totalRevenue,
    COUNT(c.id) AS totalContracts,
    SUM(so.total_quantity) AS totalVehiclesPurchased,
    NULL AS month,
    NULL AS year
FROM sale_contract c
JOIN sale_order so ON c.sale_order = so.id
JOIN quotation q ON q.sale_order_id = so.id
JOIN customer cus ON q.customer_id = cus.id
JOIN dealer d ON cus.dealer_id = d.id
WHERE c.status = 'SIGNED'
  AND d.id = UUID_TO_BIN(:dealerId)
  AND cus.id = UUID_TO_BIN(:customerId)
GROUP BY cus.id, YEAR(c.sign_date), MONTH(c.sign_date)
ORDER BY YEAR(c.sign_date), MONTH(c.sign_date)
""", nativeQuery = true)
  CustomerRevenueItemResponse getCustomerRevenueByCustomer(
          @Param("dealerId") String dealerId,
          @Param("customerId") String customerId);
}
