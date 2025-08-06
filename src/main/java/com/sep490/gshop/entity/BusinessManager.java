package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessManager extends User{
}
