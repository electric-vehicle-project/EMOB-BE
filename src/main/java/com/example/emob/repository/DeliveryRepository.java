/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.DeliveryStatus;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Delivery;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
  // ============================================================
  // 🔹 1. Hãng xe (EVM_STAFF, ADMIN) xem tất cả delivery của đại lý
  // ============================================================
  @Query(
      """
    SELECT d
    FROM Delivery d
    JOIN FETCH d.saleContract c
    JOIN c.saleOrder so
    JOIN so.vehicleRequest vr
    WHERE (:statuses IS NULL OR d.status IN :statuses)
      AND (:keyword IS NULL
           OR LOWER(CAST(d.quantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND d.isDeleted = false
""")
  Page<Delivery> searchAndFilterDeliveries(
      @Param("statuses") List<DeliveryStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // ============================================================
  // 🔹 2. Đại lý xem delivery của chính đại lý mình (qua VehicleRequest)
  // ============================================================
  @Query(
      """
  SELECT d
  FROM Delivery d
  JOIN FETCH d.saleContract c
  JOIN c.saleOrder so
  JOIN so.vehicleRequest vr
  WHERE vr.dealer = :dealer
    AND (:statuses IS NULL OR d.status IN :statuses)
    AND d.isDeleted = false
""")
  Page<Delivery> findAllWithVehicleRequestByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<DeliveryStatus> statuses,
      Pageable pageable);

  // ============================================================
  // 🔹 3. Đại lý xem delivery có báo giá cho khách hàng cụ thể
  // ============================================================
  @Query(
      """
  SELECT d
  FROM Delivery d
  JOIN FETCH d.saleContract c
  JOIN c.saleOrder so
  JOIN so.quotation q
  WHERE q.dealer = :dealer
    AND q.customer = :customer
    AND (:statuses IS NULL OR d.status IN :statuses)
    AND d.isDeleted = false
""")
  Page<Delivery> findAllWithQuotationByDealerAndCustomer(
      @Param("dealer") Dealer dealer,
      @Param("customer") Customer customer,
      @Param("statuses") List<DeliveryStatus> statuses,
      Pageable pageable);

  // ============================================================
  // 🔹 4. Đại lý xem tất cả delivery đã báo giá (mọi khách hàng)
  // ============================================================
  @Query(
      """
  SELECT d
  FROM Delivery d
  JOIN FETCH d.saleContract c
  JOIN c.saleOrder so
  JOIN so.quotation q
  WHERE q.dealer = :dealer
    AND (:statuses IS NULL OR d.status IN :statuses)
    AND d.isDeleted = false
""")
  Page<Delivery> findAllWithQuotationByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<DeliveryStatus> statuses,
      Pageable pageable);
}
