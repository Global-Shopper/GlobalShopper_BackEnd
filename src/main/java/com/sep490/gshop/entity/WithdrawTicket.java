package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "withdraw_tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawTicket extends BaseEntity{
    private String bankingBill;
    //Đây là lí do muốn rút tiền của người dùng
    private String reason;
    /**
     * denyReason sẽ null nếu status != REJECT
     * denyReason là response mà admin trả về cho khách hàng nếu admin reject rút tiền
     * [phamhminhkhoi]
     * */
    private String denyReason;
    private double amount;
    @Enumerated(EnumType.STRING)
    private WithdrawStatus status;
    private BankAccountSnapshot bankAccount;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

}
