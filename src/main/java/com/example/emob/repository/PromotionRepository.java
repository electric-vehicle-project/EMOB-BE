/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Promotion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
  @Query("""
  SELECT p FROM Promotion p
  WHERE p.scope = :scope
    AND (:statuses IS NULL OR p.status IN :statuses)
""")
  Page<Promotion> findByScopeAndOptionalStatuses(
          @Param("scope") PromotionScope scope,
          @Param("statuses") List<PromotionStatus> statuses,
          Pageable pageable
  );
  @Query("""
    SELECT p FROM Promotion p
    JOIN p.dealers d
    WHERE d.id = :dealerId
      AND (:statuses IS NULL OR p.status IN :statuses)
      AND (
            :keyword IS NULL
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
""")
  Page<Promotion> filterAndSearch(
          @Param("dealerId") UUID dealerId,
          @Param("statuses") List<PromotionStatus> statuses,
          @Param("keyword") String keyword,
          Pageable pageable
  );

  Page<Promotion> findAllByDealersContains(Dealer dealer, Pageable pageable);

  Page<Promotion> findAllByScopeAndDealersContains(
      PromotionScope scope, Dealer dealer, Pageable pageable);


  @Query("""
  SELECT p FROM Promotion p
  WHERE 
    (
      (:scopes IS NULL OR p.scope IN :scopes)
      OR :dealer MEMBER OF p.dealers
    )
    AND (:statuses IS NULL OR p.status IN :statuses)
    AND (
      :keyword IS NULL 
      OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
  Page<Promotion> findAllAndFilter(
          @Param("scopes") List<PromotionScope> scopes,
          @Param("statuses") List<PromotionStatus> statuses,
          @Param("dealer") Dealer dealer,
          @Param("keyword") String keyword,
          Pageable pageable);
}
