/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.QuotationStatus;
import com.example.emob.entity.Account;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Quotation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, UUID> {

  @Query(
      """
      SELECT q
      FROM Quotation q
      WHERE q.isDeleted = false
        AND q.dealer = :dealer
        AND q.account  = :account
        AND (
          :keyword IS NULL
          OR LOWER(CAST(q.totalQuantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
          :statuses IS NULL
          OR q.status IN :statuses
        )
    """)
  Page<Quotation> findAllByAccount(
      @Param("dealer") Dealer dealer,
      @Param("account") Account account,
      @Param("keyword") String keyword,
      @Param("statuses") List<QuotationStatus> statuses,
      Pageable pageable);

  List<Quotation> findAllByIsDeletedFalseAndStatus(QuotationStatus status);

  @Query(
      """
  SELECT q
  FROM Quotation q
  WHERE q.isDeleted = false
    AND q.dealer = :dealer
    AND (
      :keyword IS NULL
      OR LOWER(CAST(q.totalQuantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (
      :statuses IS NULL
      OR q.status IN :statuses
    )
""")
  Page<Quotation> searchAndFilter(
      @Param("dealer") Dealer dealer,
      @Param("keyword") String keyword,
      @Param("statuses") List<QuotationStatus> statuses,
      Pageable pageable);

  @Query(
          """
      SELECT q
      FROM Quotation q
      WHERE q.isDeleted = false
        AND q.dealer = :dealer
        AND q.account.id = :id
        AND (
          :keyword IS NULL
          OR LOWER(CAST(q.totalQuantity AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
          :statuses IS NULL
          OR q.status IN :statuses
        )
    """)
  Page<Quotation> searchAndFilterByDealerStaff(
          @Param("dealer") Dealer dealer,
          @Param("id") UUID AccountId,
          @Param("keyword") String keyword,
          @Param("statuses") List<QuotationStatus> statuses,
          Pageable pageable);
}
