package com.sep490.gshop.controller;

import com.sep490.gshop.common.URLConstant;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.service.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(URLConstant.WALLET)
@Log4j2
public class WalletController {

    private WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        log.info("getAllWallets() WalletController start");
        List<WalletDTO> wallets = walletService.getAllWallets();
        log.info("getAllWallets() WalletController end | Wallets size: {}", wallets.size());
        return ResponseEntity.ok().body(wallets);
    }

    @GetMapping("{id}")
    public ResponseEntity<WalletDTO> getWalletById(@PathVariable String id) {
        log.info("getWalletById() WalletController start | id: {}", id);
        WalletDTO wallet = walletService.getWalletById(id);
        log.info("getWalletById() WalletController end | Wallet: {}", wallet);
        return ResponseEntity.ok().body(wallet);
    }

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(WalletRequest walletRequest) {
        log.info("createWallet() WalletController start | walletDTO: {}", walletRequest);
        WalletDTO createdWallet = walletService.createWallet(walletRequest);
        log.info("createWallet() WalletController end | Created Wallet: {}", createdWallet);
        return ResponseEntity.ok().body(createdWallet);
    }
}
