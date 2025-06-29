package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.GenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerUpdateRequest {
    @Size(max = 100, message = "Tên không được dài quá 100 kí tự")
    private String name;

    @Email(message = "Sai định dạng mail, Ex: abc@example.com")
    @Size(max = 255, message = "Email không được vượt quá 255 kí tự")
    private String email;

    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "định dạng số điện thoại không đúng, ví dụ: +84909123456 hoặc 0912345678"
    )
    private String phone;
    private long dateOfBirth;
    private GenderEnum gender;

    private String avatar;
}
