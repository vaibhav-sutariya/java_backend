package com.infynno.javastartup.startup.modules.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.infynno.javastartup.startup.modules.services.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

    boolean existsByName(String name);
    
}
