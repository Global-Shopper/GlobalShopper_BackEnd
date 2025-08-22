package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HsTreeNodeDTO {
    private String code;             // hsCode
    private String description;
    private Integer level;
    private String parentCode;       // mã cha để gán cây
    private List<HsTreeNodeDTO> children;
    public void addChild(HsTreeNodeDTO c) {
        if (children == null) children = new ArrayList<>();
        children.add(c);
    }
}
