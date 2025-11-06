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
      JOIN FETCH s.vehicleRequest v
      WHERE (:statuses IS NULL OR s.status IN :statuses)
        AND (:keyword IS NULL
             OR LOWER(CAST(s.totalPrice AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(s.paymentStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))
  """)
  Page<SaleOrder> searchAndFilter(
      @Param("statuses") List<OrderStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // filter status, search of order and vehicle request
  @Query(
      """
    SELECT s
    FROM SaleOrder s
    JOIN FETCH s.vehicleRequest v
    WHERE v.dealer = :dealer
      AND (:statuses IS NULL OR s.status IN :statuses)
      AND (:keyword IS NULL
           OR LOWER(CAST(s.totalPrice AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(s.paymentStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleOrder> searchAndFilterByDealer(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<OrderStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  // filter status, search of order and quotation
  @Query(
      """
    SELECT s
    FROM SaleOrder s
    JOIN FETCH s.quotation q
    WHERE q.dealer = :dealer
      AND (:statuses IS NULL OR s.status IN :statuses)
      AND (:keyword IS NULL
           OR LOWER(CAST(s.totalPrice AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.paymentStatus) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(CAST(q.validUntil AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleOrder> searchAndFilterQuotedOrdersByDealer(
      @Param("dealer") Dealer dealer,
      @Param("statuses") List<OrderStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      """
    SELECT s
    FROM SaleOrder s
    JOIN FETCH s.quotation q
    WHERE s.account = :account
      AND (:statuses IS NULL OR s.status IN :statuses)
      AND (:keyword IS NULL
           OR LOWER(CAST(s.totalPrice AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(s.paymentStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleOrder> searchAndFilterByAccount(
      @Param("account") Account account,
      @Param("statuses") List<OrderStatus> statuses,
      @Param("keyword") String keyword,
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
      AND (:keyword IS NULL
           OR LOWER(CAST(s.totalPrice AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(s.paymentStatus) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
  Page<SaleOrder> searchAndFilterByDealerAndCustomer(
      @Param("dealer") Dealer dealer,
      @Param("customer") Customer customer,
      @Param("statuses") List<OrderStatus> statuses,
      @Param("keyword") String keyword,
      Pageable pageable);
}
