package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundTicketDTO {
    private String id;
    private List<String> evidence;
    private String reason;
    private double amount;
    private RefundStatus status;
}
