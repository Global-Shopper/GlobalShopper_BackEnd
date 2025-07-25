package com.sep490.gshop.utils;

import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.TaxRate;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.payload.response.TaxCalculationResult;
import com.sep490.gshop.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j

public class CalculationUtil {

    private final ExchangeRateService exchangeRateService;

    public CalculationUtil(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Tính toán các loại thuế dựa trên giá basePrice và danh sách taxRates
     * Trả về Map tên thuế -> số tiền thuế, và tổng thuế trong TaxCalculationResult
     */
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

    /**
     * Tính tổng các trường kiểu numeric (double, Double, BigDecimal) trong một entity.
     * Có thể truyền danh sách tên trường muốn tổng nếu cần (filterFields).
     * Nếu filterFields null hoặc rỗng thì tổng tất cả các trường numeric.
     */
    public BigDecimal sumNumericFields(Object entity, List<String> filterFields) {
        if (entity == null) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        Class<?> clazz = entity.getClass();

        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (filterFields != null && !filterFields.isEmpty() && !filterFields.contains(field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Class<?> type = field.getType();
                    if (type == double.class) {
                        total = total.add(BigDecimal.valueOf(field.getDouble(entity)));
                    } else if (type == Double.class) {
                        Double val = (Double) field.get(entity);
                        if (val != null) total = total.add(BigDecimal.valueOf(val));
                    } else if (type == BigDecimal.class) {
                        BigDecimal val = (BigDecimal) field.get(entity);
                        if (val != null) total = total.add(val);
                    }
                } catch (IllegalAccessException e) {
                    log.warn("Cannot access field {}: {}", field.getName(), e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return total;
    }

    /**
     * Tính tổng tiền chi tiết (basePrice + serviceFee + thuế)
     */
    public double calculateTotalPrice(double basePrice, double serviceFee, Map<String, Double> taxAmounts) {
        double total = basePrice + serviceFee;
        if (taxAmounts != null) {
            total += taxAmounts.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        return total;
    }

    /**
     * Thực hiện convert số tiền từ currency sang VND
     * Nếu currency là VND hoặc null thì trả lại tiền gốc
     */
    public BigDecimal convertToVND(BigDecimal amount, String fromCurrency) {
        if (amount == null || fromCurrency == null || fromCurrency.equalsIgnoreCase("VND")) {
            return amount != null ? amount : BigDecimal.ZERO;
        }
        try {
            CurrencyConvertResponse resp = exchangeRateService.convertToVND(amount, fromCurrency.toUpperCase());
            if (resp != null && resp.getConvertedAmount() != null) {
                return resp.getConvertedAmount();
            }
        } catch (Exception ex) {
            log.warn("Convert to VND thất bại cho {}, tiền: {}. Lỗi: {}", fromCurrency, amount, ex.getMessage());
        }
        return amount;
    }

    /**
     * Tính tổng totalVNDPrice của một danh sách QuotationDetailDTO
     */
    public double sumTotalVNDPriceOfDetails(List<?> details) {
        return details.stream()
                .mapToDouble(detail -> {
                    try {
                        return (double) detail.getClass().getMethod("getTotalVNDPrice").invoke(detail);
                    } catch (Exception e) {
                        log.warn("Cannot get totalVNDPrice from detail: {}", e.getMessage());
                        return 0d;
                    }
                })
                .sum();
    }
}
