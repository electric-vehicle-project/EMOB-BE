package com.example.emob.repository;


import com.example.emob.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Report findReportByReportId(UUID reportId);
}
