package com.sep490.gshop.service.implement;

import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.TaxRateService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TaxServiceImpl implements TaxRateService {
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

}
