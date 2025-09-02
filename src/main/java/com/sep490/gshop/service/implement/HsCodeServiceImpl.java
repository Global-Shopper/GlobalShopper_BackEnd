package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.HsCodeBusiness;
import com.sep490.gshop.business.TaxRateBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.payload.dto.HsCodeErrorResponse;
import com.sep490.gshop.payload.dto.HsTreeNodeDTO;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.ErrorImportResponse;
import com.sep490.gshop.payload.request.HsCodeListRequest;
import com.sep490.gshop.payload.request.HsCodeRequest;
import com.sep490.gshop.payload.response.ImportedResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.HsCodeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    @Override
    public Page<HsTreeNodeDTO> findAll(String hsCode, String description, int page, int size, Sort.Direction direction) {
        log.debug("findAll() Start | hsCode: {}, description: {}", hsCode, description);
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "hs_code"));

            String hsCodeSearch = (hsCode != null && !hsCode.trim().isEmpty()) ? hsCode.trim() : null;
            String descriptionSearch = (description != null && !description.trim().isEmpty()) ? description.trim() : null;

            Page<HsCode> rootsPage;

            if (hsCodeSearch != null || descriptionSearch != null) {
                rootsPage = hsCodeBusiness.searchByHsCodeAndDescriptionForRoots(hsCodeSearch, descriptionSearch, pageable);
            } else {
                rootsPage = hsCodeBusiness.getAll(pageable);
            }
            List<HsCode> all = hsCodeBusiness.getAll();
            Map<String, HsTreeNodeDTO> nodeByCode = buildNodeMap(all);
            List<HsTreeNodeDTO> roots = rootsPage.stream()
                    .map(rootHsCode -> nodeByCode.get(rootHsCode.getHsCode()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (HsTreeNodeDTO root : roots) {
                attachChildrenRecursive(root, nodeByCode);
            }
            log.debug("findAll() End | found: {}", rootsPage.getTotalElements());

            return new PageImpl<>(roots, pageable, rootsPage.getTotalElements());

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

        HsCode newHsCode = modelMapper.map(hsCodeRequest, HsCode.class);

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

        hsCodeBusiness.create(newHsCode);
        HsCodeDTO hsCodeDTO = modelMapper.map(newHsCode, HsCodeDTO.class);
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
                    .map(taxRate -> TaxRateSnapshotDTO.builder()
                            .id(taxRate.getId())
                            .hsCode(taxRate.getHsCode().getHsCode())
                            .region(taxRate.getRegion())
                            .taxType(taxRate.getTaxType())
                            .taxName(taxRate.getTaxName())
                            .rate(taxRate.getRate())
                            .build()
                    )
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

    @Transactional
    @Override
    public MessageResponse deleteHsCodePrefix(String hsCode) {
        log.debug("deleteHsCode() - Start | hsCode: {}", hsCode);
        try {
            List<HsCode> relatedCodes = hsCodeBusiness.findAllByHsCodeStartingWith(hsCode);

            if (relatedCodes.isEmpty()) {
                throw AppException.builder()
                        .message("Không tìm thấy hsCode hoặc cây con liên quan")
                        .code(404)
                        .build();
            }

            for (HsCode code : relatedCodes) {
                List<TaxRate> taxRates = taxRateBusiness.findAllByHsCode(code);
                for (TaxRate tax : taxRates) {
                    taxRateBusiness.delete(tax.getId());
                }

                boolean deleted = hsCodeBusiness.delete(code.getHsCode());
                log.debug("Đã xoá hsCode={} | deleted={}", code.getHsCode(), deleted);
            }

            return MessageResponse.builder()
                    .message("Xoá HSCode " + hsCode + " và toàn bộ cây con thành công")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("deleteHsCode() - Exception | hsCode: {}, error: {}", hsCode, e.getMessage());
            throw e;
        }
    }



    @Override
    public MessageResponse importHsCodeCSV(MultipartFile file) {
        log.debug("Start Import HSCode CSV: {}", file.getOriginalFilename());

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            List<HsCode> newParse = new ArrayList<>();
            List<String> duplicates = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {
                HsCodeDTO hsCode = HsCodeDTO.builder()
                        .hsCode(csvRecord.get("hsCode"))
                        .parentCode(csvRecord.get("parentCode"))
                        .unit(csvRecord.get("unit"))
                        .description(csvRecord.get("description"))
                        .build();

                HsCode addHsCode = modelMapper.map(hsCode, HsCode.class);

                boolean exists = hsCodeBusiness.existByHsCode(addHsCode.getHsCode());
                if (exists) {
                    duplicates.add(addHsCode.getHsCode());
                } else {
                    newParse.add(addHsCode);
                }
            }
            if (!newParse.isEmpty()) {
                hsCodeBusiness.saveAll(newParse);
            }

            log.debug("End Import HSCode CSV: {} rows imported, {} duplicates",
                    newParse.size(), duplicates.size());

            String message = "Imported: " + newParse.size() + " | Duplicates: " + duplicates.size();
            if (!duplicates.isEmpty()) {
                message += " | Duplicate hsCodes: " + String.join(", ", duplicates);
            }

            return MessageResponse.builder()
                    .message(message)
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("Error Import HSCode CSV: {}", e.getMessage());
            return MessageResponse.builder()
                    .message("Error Import HSCode: " + e.getMessage())
                    .isSuccess(false)
                    .build();
        }
    }

    @Override
    public ImportedResponse<HsCodeErrorResponse> importHsCodeNewPhase(List<HsCodeListRequest> requests) {
        log.debug("=== Start Import HSCode List, size: {} ===", requests.size());

        List<HsCode> newParse = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        List<ErrorImportResponse<HsCodeErrorResponse>> errors = new ArrayList<>();

        int importedCount = 0;
        int duplicateCount = 0;
        int errorCount = 0;

        try {
            for (HsCodeListRequest request : requests) {
                try {
                    HsCodeDTO hsCodeDTO = HsCodeDTO.builder()
                            .hsCode(request.getHsCode())
                            .parentCode(request.getParentCode())
                            .unit(request.getUnit())
                            .description(request.getDescription())
                            .build();

                    HsCode addHsCode = modelMapper.map(hsCodeDTO, HsCode.class);

                    boolean exists = hsCodeBusiness.existByHsCode(addHsCode.getHsCode());
                    if (exists) {
                        duplicates.add(addHsCode.getHsCode());
                        errors.add(new ErrorImportResponse<>(
                                modelMapper.map(addHsCode, HsCodeErrorResponse.class),
                                "Duplicate HSCode"
                        ));
                        duplicateCount++;
                    } else {
                        newParse.add(addHsCode);
                    }
                } catch (Exception ex) {
                    log.error("Lỗi khi xử lý HSCode={} | message={}", request.getHsCode(), ex.getMessage());

                    HsCodeErrorResponse failedDTO = HsCodeErrorResponse.builder()
                            .hsCode(request.getHsCode())
                            .parentCode(request.getParentCode())
                            .unit(request.getUnit())
                            .description(request.getDescription())
                            .build();

                    errors.add(new ErrorImportResponse<>(failedDTO, "Exception: " + ex.getMessage()));
                    errorCount++;
                }
            }

            if (!newParse.isEmpty()) {
                hsCodeBusiness.saveAll(newParse);
                importedCount = newParse.size();
            }

            log.debug("=== End Import HSCode List: {} imported, {} duplicates, {} errors ===",
                    importedCount, duplicateCount, errorCount);

            return ImportedResponse.<HsCodeErrorResponse>builder()
                    .success(true)
                    .message(String.format("Cập nhật HSCode xong: %d bản ghi đã được xử lý",
                            requests.size()))
                    .imported(importedCount)
                    .updated(0)
                    .duplicated(duplicateCount)
                    .errors(errors)
                    .totalRequestData(requests.size())
                    .build();

        } catch (Exception e) {
            log.error("Error Import HSCode List: {}", e.getMessage());

            return ImportedResponse.<HsCodeErrorResponse>builder()
                    .success(false)
                    .message("Lỗi import Hs Code: " + e.getMessage())
                    .errors(errors)
                    .totalRequestData(requests.size())
                    .build();
        }
    }

    private Map<String, HsTreeNodeDTO> buildNodeMap(List<HsCode> all) {
        Map<String, HsTreeNodeDTO> nodeByCode = new HashMap<>();
        for (HsCode r : all) {
            nodeByCode.put(r.getHsCode(), HsTreeNodeDTO.builder()
                    .code(r.getHsCode())
                    .description(nz(r.getDescription()))
                    .level(levelOf(r.getHsCode()))
                    .parentCode(nz(r.getParentCode()))
                    .children(new ArrayList<>())
                    .build());
        }
        return nodeByCode;
    }

    private void attachChildrenRecursive(HsTreeNodeDTO parent, Map<String, HsTreeNodeDTO> nodeByCode) {
        List<HsTreeNodeDTO> children = nodeByCode.values().stream()
                .filter(n -> parent.getCode().equals(n.getParentCode()))
                .sorted(Comparator.comparing(HsTreeNodeDTO::getCode))
                .collect(Collectors.toList());
        parent.setChildren(children);
        for (HsTreeNodeDTO child : children) {
            attachChildrenRecursive(child, nodeByCode);
        }
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    private static int levelOf(String code) {
        int len = code == null ? 0 : code.length();
        if (len >= 8) return 8;
        if (len >= 6) return 6;
        if (len >= 4) return 4;
        if (len >= 2) return 2;
        return len;
    }




}
