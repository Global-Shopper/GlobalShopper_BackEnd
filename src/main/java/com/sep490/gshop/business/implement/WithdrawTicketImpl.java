package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.WithdrawTicketBusiness;
import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.entity.WithdrawTicket;
import com.sep490.gshop.repository.WithdrawTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class WithdrawTicketImpl extends BaseBusinessImpl<WithdrawTicket, WithdrawTicketRepository> implements WithdrawTicketBusiness {
    protected WithdrawTicketImpl(WithdrawTicketRepository repository) {
        super(repository);
    }

    @Override
    public List<WithdrawTicket> findByStatus(WithdrawStatus status) {
        return repository.getWithdrawTicketsByStatus(status);
    }

    @Override
    public List<WithdrawTicket> findByWallet(UUID walletId) {
        return repository.getWithdrawTicketsByWalletId(walletId);
    }

    @Override
    public Page<WithdrawTicket> getAllByWalletId(UUID walletId, Pageable pageable) {
        return repository.findByWallet_Id(walletId, pageable);
    }
}
