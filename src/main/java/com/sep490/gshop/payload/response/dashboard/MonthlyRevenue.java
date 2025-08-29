package com.sep490.gshop.payload.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyRevenue {
    private int month;
    private double total;
    private double online;
    private double offline;
}
