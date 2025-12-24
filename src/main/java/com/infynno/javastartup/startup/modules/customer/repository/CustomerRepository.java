package com.infynno.javastartup.startup.modules.customer.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.infynno.javastartup.startup.modules.customer.model.Customer;


public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Customer> findByNameContainingIgnoreCase(String name);

    Page<Customer> findByCreatedById(String userId,Pageable pageable);

    List<Customer> findByCityIgnoreCase(String city);

    @Query("SELECT c FROM Customer c WHERE "
            + "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
            + "(:city IS NULL OR LOWER(c.city) = LOWER(:city)) AND "
            + "(:phone IS NULL OR c.phoneNumber = :phone)")
    List<Customer> searchCustomers(@Param("name") String name, @Param("city") String city,
            @Param("phone") Long phone);
}
