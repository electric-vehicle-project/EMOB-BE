package com.example.emob.entity;

import com.example.emob.constant.ReportStatus;
import jakarta.persistence.*;
import com.example.emob.constant.ReportType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", unique = true)
    UUID reportId;

    String title;
    String description;

    @Enumerated(EnumType.STRING)
    ReportType type;

    @ManyToOne
    @JoinColumn(name = "reportBy", referencedColumnName = "id")
    Customer reportBy;

    @Enumerated(EnumType.STRING)
    ReportStatus status;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
