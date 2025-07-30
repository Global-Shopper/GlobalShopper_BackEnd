package com.sep490.gshop.service;

import com.sep490.gshop.payload.response.RawDataResponse;

public interface AIService {
    String chat(String message);

    RawDataResponse getRawData(String link);
}
