package com.sep490.gshop.business;

import com.sep490.gshop.entity.Customer;

import java.util.UUID;

public interface CustomerBusiness extends BaseBusiness<Customer> {
    boolean existsByEmail(String email);
    Customer findByEmail(String email);
    Customer findByWallet(UUID walletId);
}
