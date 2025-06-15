package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;

import java.util.List;

public interface WalletService {
    List<WalletDTO> getAllWallets();

    WalletDTO createWallet(WalletRequest walletRequest);

    WalletDTO getWalletById(String id);

    WalletDTO updateWallet(String id, WalletRequest walletRequest);

    boolean deleteWallet(String id);
}
