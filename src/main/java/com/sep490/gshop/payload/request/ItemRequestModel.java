package com.sep490.gshop.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestModel {
    private String link;
    private String name;
    private List<String> variants;
    private int quantity;
    private String note;
}
