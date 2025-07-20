package com.sep490.gshop.controller;


import com.sep490.gshop.payload.request.CurrencyConvertRequest;
import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.service.ExchangeRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyConverterController {

    private final ExchangeRateService exchangeRateService;

    @PostMapping("/convert-to-vnd")
    public ResponseEntity<CurrencyConvertResponse> convertToVnd(@RequestBody @Valid CurrencyConvertRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            CurrencyConvertResponse response = exchangeRateService
                    .convertToVND(request.getAmount(), request.getCurrency());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
