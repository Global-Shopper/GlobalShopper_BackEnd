package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.entity.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(exclude = "quotation", callSuper = false)
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
    @Enumerated(EnumType.STRING)
    private SubRequestStatus status = SubRequestStatus.PENDING;
    private String rejectionReason;
    @OneToMany(mappedBy = "subRequest", fetch = FetchType.LAZY)
    private List<RequestItem> requestItems;

    @OneToOne(mappedBy = "subRequest")
    private Quotation quotation;

}
