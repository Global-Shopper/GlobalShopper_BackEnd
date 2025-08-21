package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.VariantDTO;
import com.sep490.gshop.payload.response.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface VariantService {
    VariantDTO createVariant(String name);
    List<VariantDTO> getAllVariants();
    VariantDTO getVariantById(UUID id);
    VariantDTO updateVariantById(UUID id, String newName);
    MessageResponse deleteVariantById(UUID id);

    MessageResponse toggleVariantActiveStatus(UUID id);
}
