/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.OrderStatus;
import com.example.emob.entity.Account;
import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.SaleOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, UUID> {
  @Query(
      """
  SELECT s
  FROM SaleOrder s
  JOIN FETCH s.vehicleRequest vr
  WHERE (:statuses IS NULL OR s.status IN :statuses)
""")
  Page<SaleOrder> findAllWithVehicleRequest(
      @Param("statuses") List<OrderStatus> statuses, Pageable pageable);

  @Query(
      """
  SELECT s
  FROM SaleOrder s
  JOIN FETCH s.vehicleRequest vr
  WHERE vr.dealer = :dealer
    AND (:statuses IS NULL OR s.status IN :statuses)
""")
  Page<SaleOrder> findAllWithVehicleRequestByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<OrderStatus> statuses,
      Pageable pageable);

  @Query(
      """
  SELECT s
  FROM SaleOrder s
  JOIN FETCH s.quotation q
  WHERE q.dealer = :dealer
    AND (:statuses IS NULL OR s.status IN :statuses)
""")
  Page<SaleOrder> findAllWithQuotationByDealerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<OrderStatus> statuses,
      Pageable pageable);

  @Query(
      """
      SELECT s
      FROM SaleOrder s
      JOIN FETCH s.quotation q
      WHERE q.account = :account
        AND (:statuses IS NULL OR s.status IN :statuses)
    """)
  Page<SaleOrder> findAllWithQuotationByAccountAndStatuses(
      @Param("account") Account account,
      @Param("statuses") List<OrderStatus> statuses,
      Pageable pageable);

  @Query(
      """
      SELECT s
      FROM SaleOrder s
      JOIN FETCH s.quotation q
      WHERE q.account = :account
        AND (s.status = 'COMPLETED')
    """)
  List<SaleOrder> findAllSaleOrderByAccount(@Param("account") Account account);

  @Query(
      """
  SELECT s
  FROM SaleOrder s
  JOIN FETCH s.quotation q
  WHERE q.dealer = :dealer
    AND q.customer = :customer
    AND (:statuses IS NULL OR s.status IN :statuses)
""")
  Page<SaleOrder> findAllWithQuotationByDealerAndCustomerAndStatuses(
      @Param("dealer") Dealer dealer,
      @Param("customer") Customer customer,
      @Param("statuses") List<OrderStatus> statuses,
      Pageable pageable);
}
