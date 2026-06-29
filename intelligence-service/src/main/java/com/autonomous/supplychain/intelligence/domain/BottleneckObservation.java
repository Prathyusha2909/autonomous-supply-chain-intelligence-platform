package com.autonomous.supplychain.intelligence.domain;

import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bottleneck_observations")
public class BottleneckObservation {
    @Id
    private UUID id;

    private String locationName;

    @Enumerated(EnumType.STRING)
    private ExceptionCode exceptionCode;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private Long shipmentsImpacted;
    private Double averageDwellMinutes;
    private Instant updatedAt;

    protected BottleneckObservation() {
    }

    public BottleneckObservation(String locationName, ExceptionCode exceptionCode) {
        this.id = UUID.randomUUID();
        this.locationName = locationName;
        this.exceptionCode = exceptionCode;
        this.severity = Severity.LOW;
        this.shipmentsImpacted = 0L;
        this.averageDwellMinutes = 0.0;
        this.updatedAt = Instant.now();
    }

    public void recordImpact(Integer dwellMinutes, Severity incomingSeverity) {
        long nextCount = shipmentsImpacted + 1;
        double dwell = dwellMinutes == null ? averageDwellMinutes : dwellMinutes.doubleValue();
        this.averageDwellMinutes = ((averageDwellMinutes * shipmentsImpacted) + dwell) / nextCount;
        this.shipmentsImpacted = nextCount;
        this.severity = maxSeverity(this.severity, incomingSeverity);
        this.updatedAt = Instant.now();
    }

    private Severity maxSeverity(Severity current, Severity incoming) {
        if (incoming == null) {
            return current;
        }
        return incoming.ordinal() > current.ordinal() ? incoming : current;
    }

    public UUID getId() {
        return id;
    }

    public String getLocationName() {
        return locationName;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Long getShipmentsImpacted() {
        return shipmentsImpacted;
    }

    public Double getAverageDwellMinutes() {
        return averageDwellMinutes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
