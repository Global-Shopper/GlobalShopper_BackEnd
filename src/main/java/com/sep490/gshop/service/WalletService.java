package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.dto.WithdrawTicketDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.request.WithdrawRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MessageWithBankInformationResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    MoneyChargeResponse depositMoney(WalletRequest request);
    MessageResponse withdrawMoneyRequest(@Valid WithdrawRequest request);
    WalletDTO getWalletByCurrent();
    MessageResponse processVNPayReturn(String email, String status, String amount);
    List<WithdrawTicketDTO> getWithdrawTicketsWithPendingStatus();
    MessageWithBankInformationResponse processWithdrawRequest(UUID refundTicketId, boolean isApproved, String reason);
    MessageResponse uploadTransferBill(UUID withdrawTicketId, MultipartFile multipartFile);
}
