/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.ReportStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Report;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
  Page<Report> findAllByDealer(Dealer dealer, Pageable pageable);

  @Query(
      """
    SELECT r
    FROM Report r
    WHERE r.dealer = :dealer
      AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:status IS NULL OR r.status = :status)
    """)
  Page<Report> searchAndFilter(
      @Param("dealer") Dealer dealer,
      @Param("keyword") String keyword,
      @Param("status") ReportStatus status,
      Pageable pageable);
}
