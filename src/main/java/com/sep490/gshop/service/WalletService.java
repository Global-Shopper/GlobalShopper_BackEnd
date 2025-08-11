package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.dto.WithdrawTicketDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.request.WithdrawRequest;
import com.sep490.gshop.payload.response.IPNResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MessageWithBankInformationResponse;
import com.sep490.gshop.payload.response.PaymentURLResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    PaymentURLResponse depositMoney(@Valid WalletRequest request);
    MessageResponse withdrawMoneyRequest(@Valid WithdrawRequest request);
    WalletDTO getWalletByCurrent();
    List<WithdrawTicketDTO> getWithdrawTicketsWithPendingStatus();
    MessageWithBankInformationResponse processWithdrawRequest(UUID refundTicketId, boolean isApproved, String reason);
    MessageResponse uploadTransferBill(UUID withdrawTicketId, MultipartFile multipartFile);

    IPNResponse ipnCallback(HttpServletRequest request);

    Page<WithdrawTicketDTO> getWithdrawTicketsByCurrentUser(Pageable pageable);
}
