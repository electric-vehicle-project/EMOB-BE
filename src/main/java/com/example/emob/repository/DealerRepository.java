/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Dealer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {
  Page<Dealer> findAllByIsDeletedFalse(Pageable pageable);

  @Query("""
    SELECT d
    FROM Dealer d
    WHERE d.isDeleted = false
      AND (:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.contactInfo) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.address) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:country IS NULL OR LOWER(d.country) = LOWER(:country))
    """)
    Page<Dealer> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("country") String country,
            Pageable pageable);
}
