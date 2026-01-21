package com.infynno.javastartup.startup.modules.services.model;

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
@Table(name = "services", indexes = {@Index(name = "idx_service_name", columnList = "name")})
public class Services extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String icon;

    private int price;

    private String nextService;
}
