package com.sep490.gshop.controller;


import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.request.CurrencyConvertRequest;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(URLConstant.CURRENCY)
@RequiredArgsConstructor
@Log4j2
public class CurrencyConverterController {

    private final ExchangeRateService exchangeRateService;

    @PostMapping("/convert-to-vnd")
    @Operation(summary = "Chuyển đổi ngoại tệ về VND")
    public ResponseEntity<CurrencyConvertResponse> convertToVnd(@RequestBody @Valid CurrencyConvertRequest request) {
        log.info("convertToVnd() - Start | currency: {}, amount: {}", request.getCurrency(), request.getAmount());

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("convertToVnd() - BadRequest: amount null or <= 0");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            CurrencyConvertResponse response = exchangeRateService
                    .convertToVND(request.getAmount(), request.getCurrency());
            log.info("convertToVnd() - End | from {} {} to VND = {}", request.getCurrency(), request.getAmount(), response.getConvertedAmount());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.info("convertToVnd() - IllegalArgumentException: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.info("convertToVnd() - Exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
