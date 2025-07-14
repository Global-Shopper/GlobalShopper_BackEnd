package com.sep490.gshop.repository;

import com.sep490.gshop.entity.SubRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubRequestRepository extends JpaRepository<SubRequest, UUID> {
}
