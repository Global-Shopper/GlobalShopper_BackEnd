package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
import com.sep490.gshop.entity.Transaction;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.repository.TransactionRepository;
import com.sep490.gshop.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletBusinessImpl extends BaseBusinessImpl<Wallet, WalletRepository> implements WalletBusiness {

    private TransactionRepository transactionRepository;

    @Autowired
    public WalletBusinessImpl(WalletRepository repository, TransactionRepository transactionRepository) {
        super(repository);
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Wallet checkoutOrder(double amount, Wallet wallet, UUID orderId) {
        if (wallet.getBalance() >= amount) {

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .referenceCode(orderId.toString())
                    .customer(wallet.getCustomer())
                    .balanceBefore(wallet.getBalance())
                    .balanceAfter(wallet.getBalance() - amount)
                    .description("Checkout order with ID: " + orderId)
                    .type(TransactionType.CHECKOUT)
                    .status(TransactionStatus.SUCCESS)
                    .build();
            wallet.setBalance(wallet.getBalance() - amount);
            transactionRepository.save(transaction);
            return repository.save(wallet);
        }
        return null;
    }
}
