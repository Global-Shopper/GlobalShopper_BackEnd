package com.sep490.gshop.repository;

import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.entity.WithdrawTicket;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WithdrawTicketRepository extends JpaRepository<WithdrawTicket, UUID> {
    List<WithdrawTicket> getWithdrawTicketsByStatus(WithdrawStatus status);
    List<WithdrawTicket> getWithdrawTicketsByWalletId(UUID walletId);

    Page<WithdrawTicket> findByWallet_Id(UUID walletId, Pageable pageable);

    long countByCreatedAtBetween(Long startDate, Long endDate);

    @Query("SELECT wt.status as status, COUNT(wt) as count FROM WithdrawTicket wt " +
            "WHERE wt.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY wt.status")
    List<PRStatus> countByStatus(Long startDate, Long endDate);

    Page<WithdrawTicket> findByStatus(WithdrawStatus status, Pageable pageable);

    Page<WithdrawTicket> findAll(Pageable pageable);

}
