/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.DealerPointRule;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerPointRuleRepository extends CrudRepository<DealerPointRule, String> {
  Optional<List<DealerPointRule>> findByDealerId(String dealerId);

  Optional<DealerPointRule> findDealerPointRulesByDealerIdAndMembershipLevel(
      String dealerId, String membershipLevel);

  @Query("""
      SELECT r
      FROM DealerPointRule r
      WHERE (:keyword IS NULL OR LOWER(r.membershipLevel) LIKE LOWER(CONCAT('%', :keyword, '%'))
        AND (:minPoints IS NULL OR r.minPoints >= :minPoints)
      """)
  Page<DealerPointRule> searchAndFilter(
          @Param("keyword") String keyword,
          @Param("minPoints") Integer minPoints,
          Pageable pageable);
}
