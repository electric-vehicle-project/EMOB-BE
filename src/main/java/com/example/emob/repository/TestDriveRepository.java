package com.example.emob.repository;

import com.example.emob.entity.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TestDriveRepository extends JpaRepository<TestDrive, UUID> {
    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
    FROM test_drive t
    WHERE t.salesperson = :salespersonId
      AND (:newStart < DATE_ADD(t.scheduled_at, INTERVAL 60 MINUTE)
           AND :newEnd > t.scheduled_at)
""", nativeQuery = true)
    long existsOverlap(@Param("salespersonId") UUID salePersonId,
                          @Param("newStart") LocalDateTime newStart,
                          @Param("newEnd") LocalDateTime newEnd);
}
