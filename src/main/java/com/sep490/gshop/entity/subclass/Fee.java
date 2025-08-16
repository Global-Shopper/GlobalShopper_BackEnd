package com.sep490.gshop.entity.subclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fee {
    private String feeName;
    private Double amount;
    private String currency;
}
