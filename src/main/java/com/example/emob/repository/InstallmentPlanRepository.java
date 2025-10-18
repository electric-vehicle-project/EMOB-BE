package com.example.emob.repository;

import com.example.emob.entity.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, UUID> {
    @Query("""
        SELECT p FROM InstallmentPlan p
        WHERE p.nextDueDate < :today
          AND p.status <> 'PAID'
          AND (p.lastReminderDate IS NULL OR p.lastReminderDate < :today)
        """)
    List<InstallmentPlan> findAllOverdueNeedingReminder(@Param("today") LocalDate today);
}
