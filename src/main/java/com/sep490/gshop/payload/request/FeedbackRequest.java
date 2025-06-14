package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private String comment;
    @Range(min = 1, max = 5)
    @NotBlank(message = "Rating is required")
    private int rating;
    private String orderId;
}
