package com.example.emob.entity;

import jakarta.persistence.*;
import com.example.emob.constant.ReportType;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)", unique = true)
    UUID id;

    String title;
    String content;

    @Enumerated(EnumType.STRING)
    ReportType type;

    @ManyToOne
    @JoinColumn(name = "reportBy_id")
    CustomerFeedback reportBy;
}
