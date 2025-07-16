package com.sep490.gshop.service.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.payload.response.ExchangeRateResponse;
import com.sep490.gshop.service.ExchangeRateService;
import com.sep490.gshop.service.TypedCacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
@Service
@Log4j2
public class ExchangeRateServiceImpl implements ExchangeRateService {
    @Value("${exchange-rate.api-key}")
    private String apiKey;
    @Value("${exchange-rate.url}")
    private String domain;


    private final TypedCacheService<String, ExchangeRateResponse> cacheService;

    public ExchangeRateServiceImpl(TypedCacheService<String, ExchangeRateResponse> cacheService) {
        this.cacheService = cacheService;
    }

    public ExchangeRateResponse getRates(String fromCurrency) {
        String cacheKey = fromCurrency + "_RATES";
        String requestUrl = String.format("%s/%s/latest/%s", domain, apiKey, fromCurrency);

        log.debug("getRates() Start | Gọi API tỷ giá: {}", requestUrl);
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200) {
                throw AppException.builder()
                        .message("API trả về mã lỗi: " + conn.getResponseCode())
                        .code(conn.getResponseCode())
                        .build();
            }

            ObjectMapper mapper = new ObjectMapper();
            ExchangeRateResponse response = mapper.readValue(conn.getInputStream(), ExchangeRateResponse.class);

            if (!"success".equalsIgnoreCase(response.getResult())) {
                throw AppException.builder()
                        .message("API trả về lỗi: " + response.getResult())
                        .code(403)
                        .build();
            }

            cacheService.put(CacheType.EXCHANGE_RATE, cacheKey, response);
            log.debug("getRates() End | Lưu cache tỷ giá thành công.");
            return response;

        } catch (IOException ioe) {
            log.error("getRates() IOException: {}", ioe.getMessage(), ioe);
            throw new RuntimeException("Lỗi IO khi gọi API tỷ giá", ioe);
        } catch (Exception e) {
            log.error("getRates() Exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    public CurrencyConvertResponse convertToVND(BigDecimal amount, String fromCurrency) {
        log.debug("convertToVND() Start | amount: {}, fromCurrency: {}", amount, fromCurrency);
        try {
            ExchangeRateResponse rateResponse = getRates(fromCurrency);
            Map<String, BigDecimal> rates = rateResponse.getConversionRates();

            if (!rates.containsKey(fromCurrency.toUpperCase()) || !rates.containsKey("VND")) {
                throw AppException.builder().message("Không hỗ trợ loại tiền tệ: " + fromCurrency).code(400).build();
            }

            BigDecimal rateFrom = rates.get(fromCurrency.toUpperCase());
            BigDecimal rateVnd = rates.get("VND");

            BigDecimal exchangeRate = rateVnd.divide(rateFrom, 4, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

            log.debug("convertToVND() End | convertedAmount: {}, exchangeRate: {}", convertedAmount, exchangeRate);

            return new CurrencyConvertResponse(
                    fromCurrency.toUpperCase(),
                    "VND",
                    amount,
                    convertedAmount,
                    exchangeRate
            );
        } catch (Exception e) {
            log.error("convertToVND() Exception: {}", e.getMessage(), e);
            throw e;
        }
    }
}
