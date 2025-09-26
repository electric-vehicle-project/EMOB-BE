package com.example.emob.repository;


import com.example.emob.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
//    Report findReportById(Long id);
}
