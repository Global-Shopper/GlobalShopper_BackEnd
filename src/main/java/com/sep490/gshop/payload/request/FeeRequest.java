package com.sep490.gshop.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeRequest {
    private String feeName;
    private Double amount;
}
