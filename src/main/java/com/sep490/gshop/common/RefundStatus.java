package com.sep490.gshop.common;

public enum RefundStatus {
    PENDING, // Refund request has been created but not yet processed
    APPROVED, // Refund request has been approved
    REJECTED, // Refund request has been rejected
    COMPLETED, // Refund has been successfully processed
    FAILED; // Refund processing failed due to an error or issue
}
