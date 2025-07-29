package com.sep490.gshop.common.enums;

import lombok.Getter;

@Getter
public enum DeliveryCode {
    FEDEX("FedEx"),
    UPS("UPS"),
    ;
    private final String name;

    DeliveryCode(String name) {
        this.name = name;
    }
}
