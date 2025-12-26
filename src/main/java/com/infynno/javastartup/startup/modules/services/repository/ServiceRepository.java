package com.infynno.javastartup.startup.modules.services.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infynno.javastartup.startup.modules.services.model.Services;


@Repository
public interface ServiceRepository extends JpaRepository<Services, String> {

    boolean existsByName(String name);

    Optional<Services> findById(String id);
     
}
