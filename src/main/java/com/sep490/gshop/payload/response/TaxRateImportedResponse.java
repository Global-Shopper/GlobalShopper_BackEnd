package com.sep490.gshop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxRateImportedResponse {
    boolean success;
    String message;
    int taxRateImported;
    int taxRateUpdated;
    int taxRateDuplicated;
}
