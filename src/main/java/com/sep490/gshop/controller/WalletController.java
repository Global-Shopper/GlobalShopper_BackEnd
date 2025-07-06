package com.sep490.gshop.controller;

import com.cloudinary.Url;
import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import com.sep490.gshop.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
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
    public WalletDTO getWallet() {
        log.debug("getWallet() Start");
        WalletDTO wallet = walletService.getWalletByCurrent();
        log.debug("getWallet() End | result: {}", wallet);
        return wallet;
    }
    @PostMapping
    public MoneyChargeResponse repositMoney(@RequestBody WalletRequest walletRequest) {
        log.debug("repositMoney() Start");
        var response = walletService.depositMoney(walletRequest);
        log.debug("repositMoney() End | response: {}", response);
        return response;
    }
    @GetMapping("/check-payment-vnpay")
    public MessageResponse checkPaymentVnpay(HttpServletRequest request) {
        String fullURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        String completeURL = fullURL + (queryString != null ? "?" + queryString : "");
        return walletService.processVNPayReturn(completeURL);
    }

}
