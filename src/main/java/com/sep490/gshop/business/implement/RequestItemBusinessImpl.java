package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.RequestItemBusiness;
import com.sep490.gshop.entity.RequestItem;
import com.sep490.gshop.repository.RequestItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RequestItemBusinessImpl extends BaseBusinessImpl<RequestItem, RequestItemRepository> implements RequestItemBusiness {
    public RequestItemBusinessImpl(RequestItemRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestItem> findAllById(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        // Convert String IDs to UUIDs
        List<UUID> uuidIds = ids.stream()
                .map(UUID::fromString)
                .toList();
        return repository.findAllById(uuidIds);
    }

    @Override
    public List<RequestItem> findAllBySubRequestId(UUID id) {
        return repository.findAllBySubRequestId(id);
    }
}
