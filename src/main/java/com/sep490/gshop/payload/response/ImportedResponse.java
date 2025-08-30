package com.sep490.gshop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedResponse {
    boolean success;
    String message;
    int imported;
    int updated;
    int duplicated;
}
