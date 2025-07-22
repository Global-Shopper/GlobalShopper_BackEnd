package com.sep490.gshop.payload.request;

import lombok.Data;

import java.util.List;
@Data
public class QuotationDetailBatchRequest {
    private List<QuotationDetailRequest> items;
}
