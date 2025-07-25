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

        for (TaxRate tax : taxRates) {
            TaxType type = tax.getTaxType();
            if (type == TaxType.NHAP_KHAU_TOI_UU) {
                importTaxRate = tax;
                break;
            } else if (type == TaxType.NHAP_KHAU_UU_DAI && importTaxRate == null) {
                importTaxRate = tax;
            }
        }

        if (importTaxRate != null) {
            double rate = importTaxRate.getRate();
            importTax = basePrice * rate / 100;
            taxAmountsEnum.put(importTaxRate.getTaxType(), importTax);
            taxAmounts.put(importTaxRate.getTaxType().name(), importTax);
            vatBase += importTax;
        }

        for (TaxRate tax : taxRates) {
            TaxType type = tax.getTaxType();
            if (type == TaxType.TIEU_THU_DAC_BIET) {
                double exciseRate = tax.getRate();
                exciseTax = basePrice * exciseRate / 100;
                taxAmountsEnum.put(type, exciseTax);
                taxAmounts.put(type.name(), exciseTax);
                vatBase += exciseTax;
            }
        }

        for (TaxRate tax : taxRates) {
            TaxType type = tax.getTaxType();
            if (type != TaxType.NHAP_KHAU_TOI_UU
                    && type != TaxType.NHAP_KHAU_UU_DAI
                    && type != TaxType.VAT
                    && type != TaxType.TIEU_THU_DAC_BIET) {
                double otherTax = basePrice * tax.getRate() / 100;
                taxAmountsEnum.put(type, otherTax);
                taxAmounts.put(type.name(), otherTax);
                vatBase += otherTax;
            }
        }

        for (TaxRate tax : taxRates) {
            TaxType type = tax.getTaxType();
            if (type == TaxType.VAT) {
                double vatRate = tax.getRate();
                vatTax = vatBase * vatRate / 100;
                taxAmountsEnum.put(type, vatTax);
                taxAmounts.put(type.name(), vatTax);
            }
        }

        double totalTax = taxAmounts.values().stream().mapToDouble(Double::doubleValue).sum();

        TaxCalculationResult result = new TaxCalculationResult();
        result.setTaxAmounts(taxAmounts);
        result.setTotalTax(totalTax);
        return result;
    }
}
