package com.infynno.javastartup.startup.modules.work.model;

import com.infynno.javastartup.startup.common.model.BaseEntity;
import com.infynno.javastartup.startup.modules.customer.model.Customer;
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
@Table(name = "amc_schedules", indexes = {@Index(name = "idx_amc_status", columnList = "status"),
        @Index(name = "idx_amc_customer", columnList = "customer_id")})
public class AmcSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    private WorkType workType;

    @Column(nullable = false)
    private int price;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private int collectedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;
}

