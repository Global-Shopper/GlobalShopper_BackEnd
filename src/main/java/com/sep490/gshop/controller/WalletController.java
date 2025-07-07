package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import com.sep490.gshop.service.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        log.info("getWallet() Start");
        WalletDTO wallet = walletService.getWalletByCurrent();
        log.info("getWallet() End | result: {}", wallet);
        return wallet;
    }
    @PostMapping
    public MoneyChargeResponse depositMoney(@RequestBody WalletRequest walletRequest) {
        log.info("repositMoney() Start");
        var response = walletService.depositMoney(walletRequest);
        log.info("repositMoney() End | response: {}", response);
        return response;
    }
    @GetMapping("/check-payment-vnpay")
    public MessageResponse checkPaymentVnpay(@RequestParam("email") String email,
                                             @RequestParam("vnp_ResponseCode") String status,
                                             @RequestParam("vnp_Amount") String amount) {
        var message = walletService.processVNPayReturn(email, status, amount);
        log.info("checkPaymentVnpay() End | response: {}", message);
        return message;
    }

}
