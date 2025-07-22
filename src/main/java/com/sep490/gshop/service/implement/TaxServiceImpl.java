package com.sep490.gshop.service.implement;

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
        Map<String, Double> taxAmounts = new HashMap<>();
        double importTax = 0, exciseTax = 0, vatTax = 0;
        double vatBase = basePrice;

        TaxRate importTaxRate = null;
        for (TaxRate tax : taxRates) {
            String type = tax.getTaxType().toLowerCase();
            if (type.contains("nhập khẩu tối ưu")) {
                importTaxRate = tax;
                break;
            } else if (type.contains("nhập khẩu ưu đãi") && importTaxRate == null) {
                importTaxRate = tax;
            }
        }
        if (importTaxRate != null) {
            double rate = importTaxRate.getRate();
            importTax = basePrice * rate / 100;
            taxAmounts.put(importTaxRate.getTaxType(), importTax);
            vatBase += importTax;
        }

        for (TaxRate tax : taxRates) {
            String type = tax.getTaxType().toLowerCase();
            if (type.contains("tiêu thụ đặc biệt") || type.contains("excise")) {
                double exciseRate = tax.getRate();
                exciseTax = basePrice * exciseRate / 100;
                taxAmounts.put(tax.getTaxType(), exciseTax);
                vatBase += exciseTax;
            }
        }

        for (TaxRate tax : taxRates) {
            String type = tax.getTaxType().toLowerCase();
            if (!(type.contains("nhập khẩu") || type.contains("vat") || type.contains("gtgt")
                    || type.contains("tiêu thụ đặc biệt") || type.contains("excise"))) {
                double otherTax = basePrice * tax.getRate() / 100;
                taxAmounts.put(tax.getTaxType(), otherTax);
                vatBase += otherTax;
            }
        }

        for (TaxRate tax : taxRates) {
            String type = tax.getTaxType().toLowerCase();
            if (type.contains("vat") || type.contains("gtgt")) {
                double vatRate = tax.getRate();
                vatTax = vatBase * vatRate / 100;
                taxAmounts.put(tax.getTaxType(), vatTax);
            }
        }

        double totalTax = taxAmounts.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalAfterTax = basePrice + totalTax;

        TaxCalculationResult result = new TaxCalculationResult();
        result.setTaxAmounts(taxAmounts);
        result.setTotalTax(totalTax);
        return result;
    }
}
