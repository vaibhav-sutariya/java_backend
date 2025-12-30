package com.infynno.javastartup.startup.modules.auth.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.model.VendorService;

@Repository
public interface  VendorServiceRepository extends  JpaRepository<VendorService, String> {
    boolean existsByCreatedByAndServiceId(User createdBy, String service);
   List<VendorService> findByCreatedBy(User user);
}
