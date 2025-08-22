package com.sep490.gshop.payload.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueResponse {
    private Double total;
    private Double totalOnline;
    private Double totalOffline;
}
