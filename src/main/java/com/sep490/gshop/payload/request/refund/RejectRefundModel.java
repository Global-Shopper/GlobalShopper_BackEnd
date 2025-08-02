package com.sep490.gshop.payload.request.refund;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectRefundModel {
    private String rejectionReason;
}
