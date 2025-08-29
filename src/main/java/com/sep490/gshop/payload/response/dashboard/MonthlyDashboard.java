package com.sep490.gshop.payload.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyDashboard {
    private int month;
    private DashBoardResponse dashboard;
}
