package com.sep490.gshop.service;

import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.dto.TaxRateSnapshotDTO;
import com.sep490.gshop.payload.request.TaxRateCreateAndUpdateRequest;
import com.sep490.gshop.payload.request.TaxRateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.payload.response.TaxRateImportedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaxRateService {
    TaxCalculationResult calculateTaxes(double basePrice, List<TaxRate> taxRates);
    List<TaxRateSnapshotDTO> getTaxRatesByHsCode(String hsCode);
    TaxRateSnapshotDTO updateTaxRate(String id, TaxRateCreateAndUpdateRequest taxRate);
    TaxRateSnapshotDTO getTaxRateById(String taxRateId);
    MessageResponse deleteTaxRate(String taxRateId);
    TaxRateSnapshotDTO createTaxRate(TaxRateCreateAndUpdateRequest taxRate);

    MessageResponse importTaxRatesCSV(MultipartFile file);

    TaxRateImportedResponse importTaxRatesNewPhaseCSV(List<TaxRateRequest> list);
}
