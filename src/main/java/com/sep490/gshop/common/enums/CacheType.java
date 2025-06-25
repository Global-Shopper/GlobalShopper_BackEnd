package com.sep490.gshop.common.enums;

import lombok.Getter;

@Getter
public enum CacheType {
    OTP(5),
    PAYMENT_SESSION(15),
    ORDER_CONFIRMATION(10),
    OTP_ATTEMPT(10),
    OTP_RESET_PASSWORD(5);

    private final long ttlMinutes;

    CacheType(long ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }
}
