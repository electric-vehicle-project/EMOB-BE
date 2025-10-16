/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.DealerPointRule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface DealerPointRuleRepository extends CrudRepository<DealerPointRule, String> {
  Optional<List<DealerPointRule>> findByDealerId(String dealerId);

  Optional<DealerPointRule> findDealerPointRulesByDealerIdAndMembershipLevel(
      String dealerId, String membershipLevel);
}
