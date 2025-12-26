package com.infynno.javastartup.startup.modules.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.model.VendorService;
import com.infynno.javastartup.startup.modules.services.model.Services;

@Repository
public interface  VendorServiceRepository extends  JpaRepository<VendorService, String> {
    boolean existsByCreatedByAndServiceId(User createdBy, Services service);
   List<VendorService> findByCreatedBy(User user);
}
