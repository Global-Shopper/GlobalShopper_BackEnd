package com.sep490.gshop.payload.response;

import com.sep490.gshop.payload.request.ErrorImportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedResponse<T> {
    private boolean success;
    private String message;
    private int totalRequestData;
    private int imported;
    private int updated;
    private int duplicated;
    private int errorCount;
    private List<ErrorImportResponse<T>> errors;
}

