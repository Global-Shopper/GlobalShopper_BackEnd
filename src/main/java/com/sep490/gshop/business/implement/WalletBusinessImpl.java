package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.repository.WalletRepository;
import org.springframework.stereotype.Component;

@Component
public class WalletBusinessImpl extends BaseBusinessImpl<Wallet, WalletRepository> implements WalletBusiness {

    public WalletBusinessImpl(WalletRepository repository) {
        super(repository);
    }
}
