package com.sep490.gshop.business;

import com.sep490.gshop.entity.Customer;

public interface CustomerBusiness extends BaseBusiness<Customer> {
    boolean existsByEmail(String email);
    Customer findByEmail(String email);
}
