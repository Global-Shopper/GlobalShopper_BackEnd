package com.sep490.gshop.business;

import com.sep490.gshop.entity.Wallet;

import java.util.UUID;

public interface WalletBusiness extends BaseBusiness<Wallet>{
    Wallet checkoutOrder(double amount, Wallet wallet, UUID orderId);
}
