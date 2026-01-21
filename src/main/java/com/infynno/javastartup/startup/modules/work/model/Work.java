package com.infynno.javastartup.startup.modules.work.model;

import java.time.Instant;
import com.infynno.javastartup.startup.common.model.BaseEntity;
import com.infynno.javastartup.startup.modules.customer.model.Customer;
import com.infynno.javastartup.startup.modules.services.model.Services;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "works",
        indexes = {@Index(name = "idx_work_status", columnList = "status"),
                @Index(name = "idx_work_service_date", columnList = "serviceDate"),
                @Index(name = "idx_work_customer", columnList = "customer_id")})
public class Work extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private Services serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amc_id")
    private AmcSchedule amcId;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
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
}
