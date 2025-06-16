package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sub_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubRequest extends BaseEntity {

    private String contactInfo;
    private String seller;
    private String ecommercePlatform;

    @OneToMany(mappedBy = "subRequest")
    private List<RequestItem> requestItems;
}
