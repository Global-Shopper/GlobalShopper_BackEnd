package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.HsCodeBusiness;
import com.sep490.gshop.business.TaxRateBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.payload.dto.HsCodeSearchDTO;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.HsCodeRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.HsCodeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Log4j2
public class HsCodeServiceImpl implements HsCodeService {
    private final HsCodeBusiness hsCodeBusiness;
    private TaxRateBusiness taxRateBusiness;
    private final ModelMapper modelMapper;

    public HsCodeServiceImpl(HsCodeBusiness hsCodeBusiness, ModelMapper modelMapper, TaxRateBusiness taxRateBusiness) {
        this.hsCodeBusiness = hsCodeBusiness;
        this.modelMapper = modelMapper;
        this.taxRateBusiness = taxRateBusiness;
    }

    public Page<HsCodeSearchDTO> findAll(String hsCode, String description, int page, int size, Sort.Direction direction) {
        log.debug("findAll() Start | hsCode: {}, description", hsCode, description);
        try {
            Sort sort = Sort.by(direction, "hsCode");
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "hs_code"));


            String hsCodeSearch = (hsCode != null && !hsCode.trim().isEmpty()) ? hsCode.trim() : null;
            String descriptionSearch = (description != null && !description.trim().isEmpty()) ? description.trim() : null;

            Page<HsCode> pageData = hsCodeBusiness.searchByKeyword(
                    hsCodeSearch,
                    descriptionSearch,
                    pageable
            );

            log.debug("findAll() End | found: {}", pageData.getTotalElements());

            return pageData.map(hs -> modelMapper.map(hs, HsCodeSearchDTO.class));
        } catch (Exception e) {
            log.error("findAll() Exception: {}", e.getMessage());
            throw e;
        }
    }



    @Transactional
    public HsCodeDTO createHsCodeIncludeTaxes(@Valid HsCodeRequest hsCodeRequest) {
        log.debug("createHsCodeIncludeTaxes() - Start | hsCodeRequest: {}", hsCodeRequest);

        // Kiểm tra HSCode đã tồn tại chưa
        if (hsCodeBusiness.getById(hsCodeRequest.getHsCode()).isPresent()) {
            throw AppException.builder()
                    .message("HSCode này đã có!")
                    .code(400)
                    .build();
        }

        // Map từ DTO sang Entity
        HsCode newHsCode = modelMapper.map(hsCodeRequest, HsCode.class);

        // Nếu có taxRates thì map và set quan hệ 2 chiều
        if (hsCodeRequest.getTaxRates() != null && !hsCodeRequest.getTaxRates().isEmpty()) {
            List<TaxRate> taxRateEntities = hsCodeRequest.getTaxRates().stream()
                    .map(trRequest -> {
                        TaxRate taxRate = modelMapper.map(trRequest, TaxRate.class);
                        taxRate.setHsCode(newHsCode);
                        return taxRate;
                    })
                    .collect(Collectors.toList());
            newHsCode.setTaxRates(taxRateEntities);
        }

        // Lưu HsCode cùng cascade lưu TaxRate nếu có
        hsCodeBusiness.create(newHsCode);

        // Map lại HsCode entity sang DTO để trả về, trong đó taxRates cũng được map
        HsCodeDTO hsCodeDTO = modelMapper.map(newHsCode, HsCodeDTO.class);

        // Nếu không dùng cascade hoặc muốn chắc chắn, có thể map taxRate riêng lẻ như sau:
        // List<TaxRateSnapshotDTO> taxRateDTOs = newHsCode.getTaxRates().stream()
        //       .map(trEntity -> modelMapper.map(trEntity, TaxRateSnapshotDTO.class))
        //       .collect(Collectors.toList());
        // hsCodeDTO.setTaxRates(taxRateDTOs);

        log.debug("createHsCodeIncludeTaxes() - End | hsCode: {}", hsCodeDTO.getHsCode());
        return hsCodeDTO;
    }


    @Override
    public HsCodeDTO getByHsCode(String hsCode) {
        try {
            log.debug("getByHsCode() - Start | hsCode: {}", hsCode);

            var hsCodeFound = hsCodeBusiness.getById(hsCode)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy thông tin của mã hs mà bạn cung cấp")
                            .code(404)
                            .build());

            var taxRates = taxRateBusiness.findAllByHsCode(hsCodeFound);

            HsCodeDTO hsCodeDTO = modelMapper.map(hsCodeFound, HsCodeDTO.class);

            var taxRateDTOs = taxRates.stream()
                    .map(taxRate -> modelMapper.map(taxRate, TaxRateSnapshotDTO.class))
                    .collect(Collectors.toList());

            hsCodeDTO.setTaxRates(taxRateDTOs);

            log.debug("getByHsCode() - End | hsCode: {}", hsCodeFound.getHsCode());

            return hsCodeDTO;

        } catch (Exception e) {
            log.error("getByHsCode() - Exception: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional
    public MessageResponse deleteHsCode(String hsCode) {
        log.debug("deleteHsCode() - Start | hsCode: {}", hsCode);
        try {
            HsCode hsCodeFound = hsCodeBusiness.getById(hsCode)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy hsCode bạn cung cấp")
                            .code(404)
                            .build());

            List<TaxRate> taxRateFound = taxRateBusiness.findAllByHsCode(hsCodeFound);

            for (TaxRate tax : taxRateFound) {
                taxRateBusiness.delete(tax.getId());
            }

            boolean deleted = hsCodeBusiness.delete(hsCode);

            if (!deleted) {
                log.debug("deleteHsCode() - Không thể xoá HSCode: {}", hsCode);
                return MessageResponse.builder()
                        .message("Không thể xoá được, vui lòng thử lại")
                        .isSuccess(false)
                        .build();
            }

            var taxRates = taxRateBusiness.findAllByHsCode(hsCodeFound);
            for (TaxRate tax : taxRates) {
                taxRateBusiness.delete(tax.getId());
            }

            log.debug("deleteHsCode() - End | HSCode đã xoá: {}", hsCode);
            return MessageResponse.builder()
                    .message("Xoá HSCode thành công")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("deleteHsCode() - Exception | hsCode: {}, error: {}", hsCode, e.getMessage());
            throw e;
        }
    }


}
