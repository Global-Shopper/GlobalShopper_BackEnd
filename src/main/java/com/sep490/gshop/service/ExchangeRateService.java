package com.sep490.gshop.service;

import com.sep490.gshop.payload.response.CurrencyConvertResponse;
import com.sep490.gshop.payload.response.ExchangeRateResponse;

import java.math.BigDecimal;

public interface ExchangeRateService {
    ExchangeRateResponse getRates(String fromCurrency);
    CurrencyConvertResponse convertToVND(BigDecimal amount, String fromCurrency);


}
