package com.undercontroll.infrastructure.persistence.entity;

import com.undercontroll.domain.enums.InsightGenerationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market_monthly_insight")
public class MarketMonthlyInsightJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bucket_key", nullable = false, length = 7)
    private String bucketKey;

    @Column(name = "comparison_bucket_key", length = 7)
    private String comparisonBucketKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsightGenerationStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(length = 20)
    private String provider;

    @Column(length = 100)
    private String model;

    @Column(name = "prompt_version", nullable = false, length = 32)
    private String promptVersion;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
