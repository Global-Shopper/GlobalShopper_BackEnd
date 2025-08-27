package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.HsCodeBusiness;
import com.sep490.gshop.business.TaxRateBusiness;
import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.HsCode;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.TaxRateCreateAndUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.TaxRateService;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TaxRateServiceImpl implements TaxRateService {
    private TaxRateBusiness taxRateBusiness;
    private HsCodeBusiness hsCodeBusiness;
    private ModelMapper modelMapper;
    public TaxRateServiceImpl(TaxRateBusiness taxRateBusiness, HsCodeBusiness hsCodeBusiness, ModelMapper modelMapper) {
        this.taxRateBusiness = taxRateBusiness;
        this.hsCodeBusiness = hsCodeBusiness;
        this.modelMapper = modelMapper;
    }
    @Override

    public TaxCalculationResult calculateTaxes(double basePrice, List<TaxRate> taxRates) {
        Map<TaxType, Double> taxAmountsEnum = new HashMap<>();
        Map<String, Double> taxAmounts = new HashMap<>();

        double importTax = 0, exciseTax = 0, vatTax = 0;
        double vatBase = basePrice;

        TaxRate importTaxRate = null;

        // Tìm thuế nhập khẩu có mức thấp nhất trong các loại: MFN, UKVFTA, ACFTA, v.v.
        for (TaxRate tax : taxRates) {
            TaxType type = tax.getTaxType();
            if (type == TaxType.MFN || type == TaxType.UKVFTA || type == TaxType.ACFTA || type == TaxType.VJEPA
                    || type == TaxType.AJCEP || type == TaxType.VKFTA || type == TaxType.AKFTA || type == TaxType.RCEPT) {
                if (importTaxRate == null || tax.getRate() < importTaxRate.getRate()) {
                    importTaxRate = tax;
                }
            }
        }

        // Áp thuế nhập khẩu
        if (importTaxRate != null) {
            double rate = importTaxRate.getRate();
            importTax = basePrice * rate / 100;
            taxAmountsEnum.put(importTaxRate.getTaxType(), importTax);
            taxAmounts.put(importTaxRate.getTaxType().name(), importTax);
            vatBase += importTax;
        }

        // Áp thuế tiêu thụ đặc biệt (TTDB)
        for (TaxRate tax : taxRates) {
            if (tax.getTaxType() == TaxType.TTDB) {
                double rate = tax.getRate();
                exciseTax = basePrice * rate / 100;
                taxAmountsEnum.put(TaxType.TTDB, exciseTax);
                taxAmounts.put("TTDB", exciseTax);
                vatBase += exciseTax;
            }
        }

        // Áp VAT
        for (TaxRate tax : taxRates) {
            if (tax.getTaxType() == TaxType.VAT) {
                double rate = tax.getRate();
                vatTax = vatBase * rate / 100;
                taxAmountsEnum.put(TaxType.VAT, vatTax);
                taxAmounts.put("VAT", vatTax);
            }
        }

        double totalTax = taxAmounts.values().stream().mapToDouble(Double::doubleValue).sum();

        TaxCalculationResult result = new TaxCalculationResult();
        result.setTaxAmounts(taxAmounts);
        result.setTotalTax(totalTax);
        return result;
    }

    @Override
    public List<TaxRateSnapshotDTO> getTaxRatesByHsCode(String hsCode) {
        log.debug("getTaxRatesByHsCode() - Start | hsCode: {}", hsCode);
        try {
            HsCode hsCodeEntity = hsCodeBusiness.getById(hsCode)
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy HSCode").code(404).build());

            List<TaxRate> taxRates = taxRateBusiness.findAllByHsCode(hsCodeEntity);

            List<TaxRateSnapshotDTO> dtos = taxRates.stream()
                    .map(tr -> modelMapper.map(tr, TaxRateSnapshotDTO.class))
                    .collect(Collectors.toList());

            log.debug("getTaxRatesByHsCode() - End | hsCode: {}, count: {}", hsCode, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("getTaxRatesByHsCode() - Exception | hsCode: {}, error: {}", hsCode, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public TaxRateSnapshotDTO updateTaxRate(String id, TaxRateCreateAndUpdateRequest taxRateReq) {
        log.debug("updateTaxRate() - Start | taxRateId: {}, taxRateReq: {}", id, taxRateReq);
        try {
            TaxRate taxRate = taxRateBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy TaxRate").code(404).build());

            HsCode hsCodeEntity = hsCodeBusiness.getById(taxRateReq.getHsCode())
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy HSCode").code(404).build());

            taxRate.setRegion(taxRateReq.getRegion());
            taxRate.setTaxType(taxRateReq.getTaxType());
            taxRate.setRate(taxRateReq.getRate());
            taxRate.setTaxName(taxRateReq.getTaxName());
            taxRate.setHsCode(hsCodeEntity);

            TaxRate updated = taxRateBusiness.update(taxRate);

            TaxRateSnapshotDTO dto = modelMapper.map(updated, TaxRateSnapshotDTO.class);
            log.debug("updateTaxRate() - End | taxRateId: {}", id);
            return dto;
        } catch (Exception e) {
            log.error("updateTaxRate() - Exception | taxRateId: {}, error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TaxRateSnapshotDTO getTaxRateById(String taxRateId) {
        log.debug("getTaxRateById() - Start | taxRateId: {}", taxRateId);
        try {
            TaxRate taxRate = taxRateBusiness.getById(UUID.fromString(taxRateId))
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy TaxRate").code(404).build());

            TaxRateSnapshotDTO dto = modelMapper.map(taxRate, TaxRateSnapshotDTO.class);

            log.debug("getTaxRateById() - End | taxRateId: {}", taxRateId);
            return dto;
        } catch (Exception e) {
            log.error("getTaxRateById() - Exception | taxRateId: {}, error: {}", taxRateId, e.getMessage());
            throw e;
        }
    }


    @Override
    @Transactional
    public MessageResponse deleteTaxRate(String taxRateId) {
        log.debug("deleteTaxRate() - Start | taxRateId: {}", taxRateId);
        try {
            UUID id = UUID.fromString(taxRateId);

            TaxRate taxRate = taxRateBusiness.getById(id)
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy TaxRate").code(404).build());

            taxRateBusiness.delete(taxRate.getId());

            log.debug("deleteTaxRate() - End | taxRateId: {}", taxRateId);
            return MessageResponse.builder()
                    .message("Xoá TaxRate thành công")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("deleteTaxRate() - Exception | taxRateId: {}, error: {}", taxRateId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public TaxRateSnapshotDTO createTaxRate(TaxRateCreateAndUpdateRequest taxRateReq) {
        log.debug("createTaxRate() - Start | taxRateReq: {}", taxRateReq);
        try {
            HsCode hsCodeEntity = hsCodeBusiness.getById(taxRateReq.getHsCode())
                    .orElseThrow(() -> AppException.builder().message("Không tìm thấy HSCode").code(404).build());

            boolean exists = taxRateBusiness.existsByHsCodeAndRegionAndTaxType(hsCodeEntity, taxRateReq.getRegion(), taxRateReq.getTaxType());
            if (exists) {
                throw AppException.builder()
                        .message("TaxRate đã tồn tại với khu vực và loại thuế này.")
                        .code(400)
                        .build();
            }

            TaxRate taxRate = modelMapper.map(taxRateReq, TaxRate.class);
            taxRate.setHsCode(hsCodeEntity);

            TaxRate saved = taxRateBusiness.create(taxRate);

            TaxRateSnapshotDTO dto = modelMapper.map(saved, TaxRateSnapshotDTO.class);
            log.debug("createTaxRate() - End | hsCode: {}, taxRateId: {}", taxRateReq.getHsCode(), saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("createTaxRate() - Exception | hsCode: {}, error: {}", taxRateReq.getHsCode(), e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse importTaxRatesCSV(MultipartFile file) {
        log.debug("=== Start Import Tax Rates CSV: {} ===", file.getOriginalFilename());

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
        ) {
            List<TaxRate> newParse = new ArrayList<>();
            List<String> duplicateParse = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                TaxRate tax = new TaxRate();
                tax.setTaxName(csvRecord.get("id"));
                if(csvRecord.get("id")==null) {
                    tax.setId(UUID.randomUUID());
                }
                if(csvRecord.get("taxType")!=null) {
                    tax.setTaxType(parseTaxType(csvRecord.get("taxType")));
                }
                if(csvRecord.get("region")!=null) {
                    tax.setRegion(parseRegion(csvRecord.get("region")));
                }

                if(csvRecord.get("hsCode")!=null) {
                    var hsCode = hsCodeBusiness.getById(csvRecord.get("hsCode")).orElseThrow(() -> AppException.builder().message("Không tìm thấy HSCode").code(404).build());
                    tax.setHsCode(hsCode);
                }
                tax.setTaxName(csvRecord.get("taxName"));
                tax.setRate(Double.parseDouble(csvRecord.get("rate")));
                boolean exists = taxRateBusiness.existsByHsCodeAndRegionAndTaxType(tax.getHsCode(), tax.getRegion(), tax.getTaxType());
                if(exists) {
                    duplicateParse.add(tax.getHsCode().getHsCode());
                }else {
                    newParse.add(tax);
                }
            }

            taxRateBusiness.saveAll(newParse);
            String message = "End Import Tax Rates CSV: " + newParse.size() +  " rows imported , duplicates: " +duplicateParse.size() +" rows";

            if (!duplicateParse.isEmpty()) {
                message += " | Duplicate hsCodes: " + String.join(", ", duplicateParse);
            }
            log.debug("End Import Tax Rates CSV: {} rows imported", newParse.size());

            return MessageResponse.builder()
                    .message(message)
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("Error Import Tax Rates CSV: {}", e.getMessage());
            return MessageResponse.builder()
                    .message("Error Import Tax Rates: " + e.getMessage())
                    .isSuccess(false)
                    .build();
        }
    }


    private TaxRegion parseRegion(String regionStr) {
        try {
            return TaxRegion.valueOf(regionStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid TaxRegion: " + regionStr);
        }
    }

    private TaxType parseTaxType(String typeStr) {
        try {
            return TaxType.valueOf(typeStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid TaxType: " + typeStr);
        }
    }

}
