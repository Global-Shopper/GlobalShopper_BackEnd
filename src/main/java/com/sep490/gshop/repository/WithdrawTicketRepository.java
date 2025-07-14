package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.entity.WithdrawTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WithdrawTicketRepository extends JpaRepository<WithdrawTicket, UUID> {
    List<WithdrawTicket> getWithdrawTicketsByStatus(WithdrawStatus status);
    List<WithdrawTicket> getWithdrawTicketsByWalletId(UUID walletId);
}
