package com.sep490.gshop.business;

import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.entity.WithdrawTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WithdrawTicketBusiness extends BaseBusiness<WithdrawTicket>{
    List<WithdrawTicket> findByStatus(WithdrawStatus status);
    List<WithdrawTicket> findByWallet(UUID walletId);
    Page<WithdrawTicket> getAllByWalletId(UUID walletId, Pageable pageable);
}
