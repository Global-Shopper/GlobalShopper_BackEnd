package com.sep490.gshop.repository;

import com.sep490.gshop.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, UUID> {
    FCMToken findByTokenAndUserId(String token, UUID userId);
}
