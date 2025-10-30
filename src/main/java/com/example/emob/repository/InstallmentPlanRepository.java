/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.InstallmentStatus;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.InstallmentPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, UUID> {
  @Query(
      """
        SELECT p FROM InstallmentPlan p
        WHERE p.nextDueDate < :today
          AND p.status <> 'PAID'
          AND (p.lastReminderDate IS NULL OR p.lastReminderDate < :today)
        """)
  List<InstallmentPlan> findAllOverdueNeedingReminder(@Param("today") LocalDate today);

  // ============================================================
  // 🔹 1. Hãng xe (EVM_STAFF, ADMIN) xem tất cả InstallmentPlan của đại lý
  // ============================================================
  @Query("""
  SELECT ip
  FROM InstallmentPlan ip
  JOIN FETCH ip.saleOrder so
  JOIN FETCH so.vehicleRequest vr
  WHERE (:statuses IS NULL OR ip.status IN :statuses)
    AND (
         :keyword IS NULL
         OR CAST(ip.termMonths AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.totalAmount AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.monthlyAmount AS string) LIKE CONCAT('%', :keyword, '%')
    )
""")
  Page<InstallmentPlan> searchAndFilterWithVehicleRequest(
          @Param("statuses") List<InstallmentStatus> statuses,
          @Param("keyword") String keyword,
          Pageable pageable);

  // ============================================================
  // 🔹 2. Đại lý xem InstallmentPlan của chính đại lý mình (qua VehicleRequest)
  // ============================================================
  @Query(
      """
    SELECT ip
    FROM InstallmentPlan ip
    JOIN FETCH ip.saleOrder so
    JOIN FETCH so.vehicleRequest vr
    WHERE  vr.dealer = :dealer
      AND (:statuses IS NULL OR so.status IN :statuses)
          AND (
         :keyword IS NULL
         OR CAST(ip.termMonths AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.totalAmount AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.monthlyAmount AS string) LIKE CONCAT('%', :keyword, '%')
         )
    """)
  Page<InstallmentPlan> findAllWithVehicleRequestByDealerAndStatuses(
       @Param("keyword") String keyword,
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<InstallmentStatus> statuses,
      Pageable pageable);

  // ============================================================
  // 🔹 3. Đại lý xem InstallmentPlan của khách hàng cụ thể
  // ============================================================
  @Query(
      """
    SELECT ip
    FROM InstallmentPlan ip
    JOIN FETCH ip.saleOrder so
    JOIN FETCH so.quotation q
    WHERE  q.dealer = :dealer
      AND q.customer = :customer
      AND (:statuses IS NULL OR so.status IN :statuses)
                AND (
         :keyword IS NULL
         OR CAST(ip.termMonths AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.totalAmount AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.monthlyAmount AS string) LIKE CONCAT('%', :keyword, '%')
         )
    """)
  Page<InstallmentPlan> findAllWithQuotationByDealerAndCustomer(
          @Param("keyword") String keyword,
      @Param("dealer") Dealer dealer,
      @Param("customer") Customer customer,
      @Param("statuses") List<InstallmentStatus> statuses,
      Pageable pageable);

  // ============================================================
  // 🔹 4. Đại lý xem tất cả InstallmentPlan đã báo giá (mọi khách hàng)
  // ============================================================
  @Query(
      """
    SELECT ip
    FROM InstallmentPlan ip
    JOIN FETCH ip.saleOrder so
    JOIN FETCH so.quotation q
    WHERE q.dealer = :dealer
      AND (:statuses IS NULL OR so.status IN :statuses)
                      AND (
         :keyword IS NULL
         OR CAST(ip.termMonths AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.totalAmount AS string) LIKE CONCAT('%', :keyword, '%')
         OR CAST(ip.monthlyAmount AS string) LIKE CONCAT('%', :keyword, '%')
         )
    """)
  Page<InstallmentPlan> findAllWithQuotationByDealerAndStatuses(
          @Param("keyword") String keyword,
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<InstallmentStatus> statuses,
      Pageable pageable);
}
