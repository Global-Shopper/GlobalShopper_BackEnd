package com.sep490.gshop.repository;

import com.sep490.gshop.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> {
        List<ShippingAddress> findByCustomerId(UUID userId);
}
