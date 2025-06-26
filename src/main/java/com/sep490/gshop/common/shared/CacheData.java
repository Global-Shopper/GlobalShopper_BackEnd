package com.sep490.gshop.common.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CacheData<T> {
    private T data;
    private long expiredTime;
}
