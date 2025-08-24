package com.sep490.gshop.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sep490.gshop.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    private UserDTO user;
}
