package com.sep490.gshop.payload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sep490.gshop.common.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundTicketDTO {
    private String id;
    private List<String> evidence;
    private String reason;
    private double amount;
    private String rejectionReason;
    private Double refundRate;
    private RefundStatus status;
}
