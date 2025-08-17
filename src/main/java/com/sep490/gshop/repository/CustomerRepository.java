package com.sep490.gshop.repository;

import com.sep490.gshop.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    Customer findByEmailIgnoreCase(String email);
    Customer findByWalletId(UUID walletId);

    Page<Customer> findAll(Specification<Customer> spec, Pageable pageable);
}
