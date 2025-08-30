package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration extends BaseEntity {
    private double serviceFee;
}
