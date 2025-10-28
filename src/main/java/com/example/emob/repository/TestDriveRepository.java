/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.TestStatus;
import com.example.emob.entity.TestDrive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestDriveRepository extends JpaRepository<TestDrive, UUID> {
  @Query(
      value =
          """
    SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
    FROM test_drive t
    WHERE t.salesperson = :salespersonId
      AND (:newStart < DATE_ADD(t.scheduled_at, INTERVAL 60 MINUTE)
           AND :newEnd > t.scheduled_at)
""",
      nativeQuery = true)
  long existsOverlap(
      @Param("salespersonId") UUID salePersonId,
      @Param("newStart") LocalDateTime newStart,
      @Param("newEnd") LocalDateTime newEnd);

  @Query(
      """
    SELECT t
    FROM TestDrive t
    JOIN t.customer c
    WHERE (
      :keyword IS NULL
      OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      OR LOWER(t.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (
      :statuses IS NULL
      OR t.status IN :statuses
    )
""")
  Page<TestDrive> searchAndFilter(
      @Param("keyword") String keyword,
      @Param("statuses") List<TestStatus> statuses,
      Pageable pageable);
}
