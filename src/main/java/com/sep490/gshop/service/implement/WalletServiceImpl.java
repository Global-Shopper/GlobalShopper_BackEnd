package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.service.WalletService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {

    private final WalletBusiness walletBusiness;
    private final CustomerBusiness customerBusiness;
    private final ModelMapper modelMapper;
    private VNPayServiceImpl vnPayServiceImpl;
    @Autowired
    public WalletServiceImpl(WalletBusiness walletBusiness, CustomerBusiness customerBusiness, ModelMapper modelMapper, VNPayServiceImpl vnPayServiceImpl) {
        this.walletBusiness = walletBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
        this.vnPayServiceImpl = vnPayServiceImpl;
    }

    @Override
    public List<WalletDTO> getAllWallets() {
        try {
            log.debug("getAllWallets() WalletServiceImpl start");
            List<WalletDTO> wallets = walletBusiness.getAll().stream()
                    .map(wallet -> modelMapper.map(wallet, WalletDTO.class))
                    .toList();
            log.debug("getAllWallets() WalletServiceImpl end | Wallets size: {}", wallets.size());
            return wallets;
        } catch (Exception e) {
            log.error("getAllWallets() WalletServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public WalletDTO createWallet(WalletRequest walletRequest) {
        try {
            log.debug("createWallet() WalletServiceImpl start | walletRequest: {}", walletRequest);
            Customer customer = customerBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> new AppException(404,"Customer not found"));
            Wallet wallet = Wallet.builder()
                    .customer(customer)
                    .balance(walletRequest.getBalance())
                    .build();
            var deposit = vnPayServiceImpl.createURL(wallet.getBalance());
            WalletDTO createdWallet = modelMapper.map(walletBusiness.create(wallet), WalletDTO.class);
            createdWallet.setUrl(deposit);
            log.debug("createWallet() WalletServiceImpl end | Created Wallet: {}", createdWallet);
            return createdWallet;
        } catch (Exception e) {
            log.error("createWallet() WalletServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public WalletDTO getWalletById(String id) {
        try {
            log.debug("getWalletById() WalletServiceImpl start | id: {}", id);
            Wallet wallet = walletBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Wallet not found"));
            WalletDTO walletDTO = modelMapper.map(wallet, WalletDTO.class);
            log.debug("getWalletById() WalletServiceImpl end | Wallet: {}", walletDTO);
            return walletDTO;
        } catch (Exception e) {
            log.error("getWalletById() WalletServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public WalletDTO updateWallet(String id, WalletRequest walletRequest) {
        try {
            log.debug("updateWallet() WalletServiceImpl start | id: {}, walletRequest: {}", id, walletRequest);
            Wallet existingWallet = walletBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Wallet not found"));
            Customer customer = customerBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Customer not found"));
            existingWallet.setCustomer(customer);
            existingWallet.setBalance(walletRequest.getBalance());
            Wallet updatedWallet = walletBusiness.update(existingWallet);
            WalletDTO updatedWalletDTO = modelMapper.map(updatedWallet, WalletDTO.class);
            log.debug("updateWallet() WalletServiceImpl end | Updated Wallet: {}", updatedWalletDTO);
            return updatedWalletDTO;
        } catch (Exception e) {
            log.error("updateWallet() WalletServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteWallet(String id) {
        try {
            log.debug("deleteWallet() WalletServiceImpl start | id: {}", id);
            Wallet wallet = walletBusiness.getById(UUID.fromString(id))
                    .orElseThrow(() -> new AppException(404, "Wallet not found"));
            Customer customer = customerBusiness.getById(wallet.getCustomer().getId())
                    .orElseThrow(() -> new AppException(404, "Customer not found"));
            customer.setWallet(null);
            customerBusiness.update(customer);
            boolean check = walletBusiness.delete(UUID.fromString(id));
            log.debug("deleteWallet() WalletServiceImpl end | Wallet with id: {} deleted", id);
            return check;
        } catch (Exception e) {
            log.error("deleteWallet() WalletServiceImpl Error | message: {}", e.getMessage(), e);
            throw e;
        }
    }

}
