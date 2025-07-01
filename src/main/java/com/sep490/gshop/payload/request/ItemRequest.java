package com.sep490.gshop.payload.request;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
    private String productName;
    private String productSpecification;
    private List<String> images;
    private String description;

}
