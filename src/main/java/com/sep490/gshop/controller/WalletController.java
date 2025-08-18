package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.payload.dto.WithdrawTicketDTO;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.request.WithdrawRequest;
import com.sep490.gshop.payload.response.IPNResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MessageWithBankInformationResponse;
import com.sep490.gshop.payload.response.PaymentURLResponse;
import com.sep490.gshop.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Operation(summary = "Lấy thông tin ví của người dùng hiện tại")
    public ResponseEntity<WalletDTO> getWallet() {
        log.info("getWallet() Start");
        try {
            WalletDTO wallet = walletService.getWalletByCurrent();
            log.info("getWallet() End | result: {}", wallet);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            log.error("getWallet() Exception | message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Nạp tiền vào ví người dùng hiện tại")
    public ResponseEntity<PaymentURLResponse> depositMoney(@Valid @RequestBody WalletRequest walletRequest) {
        log.info("depositMoney() Start | request: {}", walletRequest);
        try {
            PaymentURLResponse response = walletService.depositMoney(walletRequest);
            log.info("depositMoney() End | response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("depositMoney() Exception | message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-payment-vnpay")
    @Operation(summary = "Kiểm tra trạng thái thanh toán VNPay và chuyển hướng")
    public ResponseEntity<Boolean> checkPaymentVNPay(
            @RequestParam("email") String email,
            @RequestParam("vnp_ResponseCode") String status,
            @RequestParam("vnp_Amount") String amount,
            @RequestParam("vnp_TxnRef") String txnRef) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", domainRedirect + "/wallet/deposit" + "?vnp_ResponseCode=" + status);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/ipn")
    @Operation(summary = "IPN callback từ VNPay")
    public ResponseEntity<IPNResponse> ipnCallback(HttpServletRequest request) {
        log.info("IPN Callback Start");
        IPNResponse response = walletService.ipnCallback(request);
        log.info("IPN Callback End | response: {}", response);
        return ResponseEntity.ok(response);
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
            log.error("POST /api/wallet/withdraw Exception | message: {}", e.getMessage());
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
            log.error("GET /api/refund-tickets/withdraw Exception | message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/withdraw-customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PageableAsQueryParam
    @Operation(summary = "Lấy tất cả các request rút tiền của người dùng hiện tại")
    public ResponseEntity<Page<WithdrawTicketDTO>> getWithdrawTicketsByCurrentUser(@ParameterObject Pageable pageable) {
        log.info("GET /api/withdraw-requests/withdraw-customer Start");
        Page<WithdrawTicketDTO> tickets = walletService.getWithdrawTicketsByCurrentUser(pageable);
        log.info("GET /api/withdraw-requests/withdraw-customer End | found {}", tickets.getSize());
        return ResponseEntity.ok(tickets);
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
            log.error("processWithdraw() Exception | message: {}", e.getMessage());
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
            log.error("uploadTransferBill() Controller Exception | withdrawTicketId: {}, message: {}", withdrawTicketId, e.getMessage());
            return ResponseEntity.status(500).body(
                    MessageResponse.builder()
                            .isSuccess(false)
                            .message("Lỗi khi upload hóa đơn chuyển khoản: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/admin/withdraw-tickets")
    public Page<WithdrawTicketDTO> getAllWithdrawTicketsForAdmin(
            @RequestParam(required = false) WithdrawStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("getAllWithdrawTicketsForAdmin() - START | page: {}, size: {}, status: {}", page, size, status);
        Page<WithdrawTicketDTO> result = walletService.getAllWithdrawTicketsForAdmin(page, size, status);
        log.info("getAllWithdrawTicketsForAdmin() - END | returned {} records", result.getNumberOfElements());
        return result;
    }

}
