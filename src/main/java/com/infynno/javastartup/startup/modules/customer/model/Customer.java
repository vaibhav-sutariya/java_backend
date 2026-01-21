package com.infynno.javastartup.startup.modules.customer.model;

import com.infynno.javastartup.startup.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
@Table(name = "customers",
        indexes = {@Index(name = "idx_customer_phone", columnList = "phoneNumber"),
                @Index(name = "idx_customer_name", columnList = "name"),
                @Index(name = "idx_customer_city", columnList = "city")})
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    // inherited: createdBy, updatedBy, createdAt, updatedAt, id
}
