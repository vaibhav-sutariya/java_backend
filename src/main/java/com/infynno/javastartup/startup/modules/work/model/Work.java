package com.infynno.javastartup.startup.modules.work.model;

import java.time.Instant;
import org.hibernate.annotations.UuidGenerator;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.customer.model.Customer;
import com.infynno.javastartup.startup.modules.services.model.Service;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customerId;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="service_id", nullable=false)
    private Service serviceId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="amc_id")
    private AmcSchedule amcId;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type",nullable = false)
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Column(nullable = false)
    private Instant serviceDate;

    @Column(nullable = false)
    private int price;

    private String modelName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private int sequence;

    @Column(columnDefinition = "TEXT")
    private String replacedParts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now(); 
    }

}
