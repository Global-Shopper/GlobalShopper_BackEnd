package com.sep490.gshop.repository;

import com.sep490.gshop.entity.RequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, UUID> {
    List<RequestItem> findAllBySubRequestId(UUID id);


}
