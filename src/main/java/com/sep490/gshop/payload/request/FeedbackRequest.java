package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String comment;
    @Range(min = 1, max = 5, message = "Đánh giá phải từ 1 đến 5 sao")
    @NotBlank(message = "Cần chọn ít nhất một đánh giá từ 1 đến 5 sao")
    private int rating;
    @NotNull(message = "Mã đơn hàng không được để trống")
    private String orderId;
}
