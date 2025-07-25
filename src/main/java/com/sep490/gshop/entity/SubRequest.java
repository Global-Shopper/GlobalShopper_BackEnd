package com.sep490.gshop.entity;

import com.sep490.gshop.entity.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sub_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubRequest extends BaseEntity {

    @Convert(converter = StringListConverter.class)
    private List<String> contactInfo;
    private String seller;
    private String ecommercePlatform;

    @OneToMany(mappedBy = "subRequest", fetch = FetchType.LAZY)
    private List<RequestItem> requestItems;

    @OneToOne(mappedBy = "subRequest", fetch = FetchType.LAZY)
    private Quotation quotation;

}
