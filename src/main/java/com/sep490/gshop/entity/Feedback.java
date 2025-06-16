package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends BaseEntity{
    @Column(columnDefinition = "TEXT")
    private String comment;
    private int rating;
    private boolean isDeleted;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
