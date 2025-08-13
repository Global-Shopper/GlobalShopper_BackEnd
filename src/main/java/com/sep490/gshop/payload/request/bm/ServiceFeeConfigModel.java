package com.sep490.gshop.payload.request.bm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceFeeConfigModel {
    private Double serviceFee;
}
