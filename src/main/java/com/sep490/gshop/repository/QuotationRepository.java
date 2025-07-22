package com.sep490.gshop.repository;

import com.sep490.gshop.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    Optional<Quotation> findBySubRequest_Id(UUID subRequestId);
}
