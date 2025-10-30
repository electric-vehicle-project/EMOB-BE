/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.ContractStatus;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.SaleContract;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleContractRepository extends JpaRepository<SaleContract, UUID> {
  // ============================================================
  // 🔹 1. Hãng xe (EVM_STAFF, ADMIN) xem tất cả contract
  // ============================================================
  @Query(
      """
      SELECT c
      FROM SaleContract c
      JOIN FETCH c.saleOrder so
      JOIN so.vehicleRequest vr
      WHERE (:statuses IS NULL OR c.status IN :statuses)
        AND (:keyword IS NULL OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
  Page<SaleContract> findAllWithVehicleRequest(
      @Param("statuses") List<ContractStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // ============================================================
  // 🔹 2. Đại lý xem contract của chính đại lý mình (qua VehicleRequest)
  // ============================================================
  @Query(
      """
  SELECT c
  FROM SaleContract c
  JOIN FETCH c.saleOrder so
  JOIN so.vehicleRequest vr
  WHERE vr.dealer = :dealer
    AND (:statuses IS NULL OR c.status IN :statuses)
    AND (:keyword IS NULL OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleContract> findAllWithVehicleRequestByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<ContractStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // ============================================================
  // 🔹 3. Đại lý xem contract có báo giá cho khách hàng cụ thể
  // ============================================================
  @Query(
      """
  SELECT c
  FROM SaleContract c
  JOIN FETCH c.saleOrder so
  JOIN so.quotation q
  WHERE q.dealer = :dealer
    AND q.customer = :customer
    AND (:statuses IS NULL OR c.status IN :statuses)
    AND (:keyword IS NULL OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleContract> findAllWithQuotationByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("customer") Customer customer,
      @Param("statuses") List<ContractStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // ============================================================
  // 🔹 4. Đại lý xem toàn bộ contract đã báo giá (mọi khách hàng)
  // ============================================================
  @Query(
      """
  SELECT c
  FROM SaleContract c
  JOIN FETCH c.saleOrder so
  JOIN so.quotation q
  WHERE q.dealer = :dealer
    AND (:statuses IS NULL OR c.status IN :statuses)
    AND (:keyword IS NULL OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleContract> findAllWithQuotationByDealerAndCustomerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<ContractStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);
}
