package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.WithdrawTicketDTO;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.request.WithdrawRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MessageWithBankInformationResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import com.sep490.gshop.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping(URLConstant.WALLET)
@Log4j2
@CrossOrigin("*")
public class WalletController {

    private final WalletService walletService;
    @Value("${fe.redirect-domain}")
    private String domainRedirect;
    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<WalletDTO> getWallet() {
        log.info("getWallet() Start");
        try {
            WalletDTO wallet = walletService.getWalletByCurrent();
            log.info("getWallet() End | result: {}", wallet);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            log.error("getWallet() Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<MoneyChargeResponse> depositMoney(@Valid @RequestBody WalletRequest walletRequest) {
        log.info("depositMoney() Start | request: {}", walletRequest);
        try {
            MoneyChargeResponse response = walletService.depositMoney(walletRequest);
            log.info("depositMoney() End | response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("depositMoney() Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-payment-vnpay")
    public ResponseEntity<Boolean> checkPaymentVNPay(
            @RequestParam("email") String email,
            @RequestParam("vnp_ResponseCode") String status,
            @RequestParam("vnp_Amount") String amount) {
        var check = walletService.processVNPayReturn(email, status, amount);
        if(check) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", domainRedirect + "/wallet/deposit" + "?vnp_ResponseCode=" + status);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }else {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", domainRedirect + "/wallet/deposit" + "?vnp_ResponseCode=" + status);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/ipn")
    @Operation(summary = "IPN callback from VNPay")
    public ResponseEntity<String> ipnCallback(HttpServletRequest request) {
        log.info("IPN Callback Start");
        try {
            log.info("IPN Callback Request | parameters: {}", Collections.list(request.getParameterNames()));
            return ResponseEntity.ok("IPN Callback received successfully.");
        } catch (Exception e) {
            log.error("IPN Callback Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing IPN callback.");
        }
    }

    @PostMapping("/withdraw-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Request rút tiền bởi role customer")
    public ResponseEntity<MessageResponse> withdrawMoney(@Valid @RequestBody WithdrawRequest request) {
        log.info("POST /api/wallet/withdraw Start | request: {}", request);
        try {
            MessageResponse response = walletService.withdrawMoneyRequest(request);
            log.info("POST /api/wallet/withdraw End | response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("POST /api/wallet/withdraw Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/withdraw")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả các request rút tiền ở trạng thái PENDING bởi role admin")
    public ResponseEntity<List<WithdrawTicketDTO>> getWithdrawTickets() {
        log.info("GET /api/refund-tickets/withdraw Start");
        try {
            List<WithdrawTicketDTO> tickets = walletService.getWithdrawTicketsWithPendingStatus();
            log.info("GET /api/refund-tickets/withdraw End | found {} tickets", tickets.size());
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("GET /api/refund-tickets/withdraw Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/{withdrawTicketId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xử lý rút tiền bởi role admin")
    public ResponseEntity<MessageWithBankInformationResponse> processWithdraw(
            @PathVariable UUID withdrawTicketId,
            @RequestParam boolean isApproved,
            @RequestParam(required = false) String reason) {
        log.info("POST /api/withdraw-requests/{}/process | isApproved: {}, reason: {}", withdrawTicketId, isApproved, reason);
        try {
            MessageWithBankInformationResponse response = walletService.processWithdrawRequest(withdrawTicketId, isApproved, reason);
            log.info("processWithdraw() End | isSuccess: {}, message: {}", response.isSuccess(), response.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("processWithdraw() Exception | message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/{withdrawTicketId}/upload-bill", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload hóa đơn chuyển khoản cho yêu cầu rút tiền bởi admin")
    public ResponseEntity<MessageResponse> uploadTransferBill(
            @PathVariable UUID withdrawTicketId,
            @RequestPart("file") MultipartFile multipartFile) {
        log.info("uploadTransferBill() Controller Start | withdrawTicketId: {}, filename: {}", withdrawTicketId, multipartFile.getOriginalFilename());

        try {
            MessageResponse response = walletService.uploadTransferBill(withdrawTicketId, multipartFile);

            log.info("uploadTransferBill() Controller End | isSuccess: {}, message: {}", response.isSuccess(), response.getMessage());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("uploadTransferBill() Controller Exception | withdrawTicketId: {}, message: {}", withdrawTicketId, e.getMessage(), e);
            return ResponseEntity.status(500).body(
                    MessageResponse.builder()
                            .isSuccess(false)
                            .message("Lỗi khi upload hóa đơn chuyển khoản: " + e.getMessage())
                            .build());
        }
    }
}
