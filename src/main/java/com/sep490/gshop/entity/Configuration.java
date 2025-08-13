package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration extends BaseEntity {
    private double serviceFee;
}
