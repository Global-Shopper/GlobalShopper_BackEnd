package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.VariantBusiness;
import com.sep490.gshop.entity.Variant;
import com.sep490.gshop.payload.dto.VariantDTO;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.VariantService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class VariantServiceImpl  implements VariantService {
    private VariantBusiness variantBusiness;
    private ModelMapper modelMapper;
    public VariantServiceImpl(VariantBusiness variantBusiness, ModelMapper modelMapper) {
        this.variantBusiness = variantBusiness;
        this.modelMapper = modelMapper;
    }
    @Override
    public VariantDTO createVariant(String name) {
        log.debug("createVariant() - Start | name: {}", name);
        try {
            Variant variant = new Variant();
            variant.setName(name);
            Variant saved = variantBusiness.create(variant);
            VariantDTO dto = modelMapper.map(saved, VariantDTO.class);
            log.debug("createVariant() - End | id: {}", dto.getId());
            return dto;
        } catch (Exception e) {
            log.error("createVariant() - Exception | name: {}, error: {}", name, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<VariantDTO> getAllVariants() {
        log.debug("getAllVariants() - Start");
        try {
            List<Variant> variants = variantBusiness.getAll();
            List<VariantDTO> dtos = variants.stream()
                    .map(v -> modelMapper.map(v, VariantDTO.class))
                    .collect(Collectors.toList());
            log.debug("getAllVariants() - End | count: {}", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("getAllVariants() - Exception: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public VariantDTO getVariantById(UUID id) {
        log.debug("getVariantById() - Start | id: {}", id);
        try {
            Variant variant = variantBusiness.getById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant với id: " + id));
            VariantDTO dto = modelMapper.map(variant, VariantDTO.class);
            log.debug("getVariantById() - End | id: {}", id);
            return dto;
        } catch (Exception e) {
            log.error("getVariantById() - Exception | id: {}, error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public VariantDTO updateVariantById(UUID id, String newName) {
        log.debug("updateVariantById() - Start | id: {}, newName: {}", id, newName);
        try {
            Variant variant = variantBusiness.getById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy variant với id: " + id));
            variant.setName(newName);
            Variant updated = variantBusiness.update(variant);
            VariantDTO dto = modelMapper.map(updated, VariantDTO.class);
            log.debug("updateVariantById() - End | id: {}", id);
            return dto;
        } catch (Exception e) {
            log.error("updateVariantById() - Exception | id: {}, error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse deleteVariantById(UUID id) {
        log.debug("deleteVariantById() - Start | id: {}", id);
        try {
            boolean deleted = variantBusiness.delete(id);
            if (!deleted) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Không thể xoá variant, vui lòng thử lại")
                        .build();
            }
            log.debug("deleteVariantById() - End | id: {}", id);
            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Xoá variant thành công")
                    .build();
        } catch (Exception e) {
            log.error("deleteVariantById() - Exception | id: {}, error: {}", id, e.getMessage());
            throw e;
        }
    }



}
