/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Report;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {}
