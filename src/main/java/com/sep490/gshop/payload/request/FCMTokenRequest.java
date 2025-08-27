package com.sep490.gshop.payload.request;

import com.sep490.gshop.entity.FCMToken;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FCMTokenRequest {
    @NotNull(message = "Token không được để trống")
    private String token;
    @NotNull(message = "Loại thiết bị không được để trống")
    private FCMToken.DeviceType deviceType;
}
