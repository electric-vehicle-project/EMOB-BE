/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.PromotionScope;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Promotion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
  Page<Promotion> findByScope(PromotionScope scope, Pageable pageable);

  List<Promotion> findAllByDealersId(UUID dealerId);

  Page<Promotion> findAllByDealersContains(Dealer dealer, Pageable pageable);

  Page<Promotion> findAllByScopeAndDealersContains(
      PromotionScope scope, Dealer dealer, Pageable pageable);

  Page<Promotion> findAllByScopeOrDealersContains(
      PromotionScope scope, Dealer dealer, Pageable pageable);
}
