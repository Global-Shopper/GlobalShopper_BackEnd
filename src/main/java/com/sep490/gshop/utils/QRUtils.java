package com.sep490.gshop.utils;

import com.sep490.gshop.entity.RefundTicket;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QRUtils {

    private static final String BASE_URL = "https://api.vietqr.io/image";
    private static final String PATH_PATTERN = "%s-%s-4vvJZJC.jpg";
    private static final String QUERY_PARAM_ACCOUNT_NAME = "accountName";
    private static final String QUERY_PARAM_AMOUNT = "amount";

    public static String generateQrLink(RefundTicket refundTicket) {
        if (refundTicket == null || refundTicket.getBankAccount() == null) {
            throw new IllegalArgumentException("RefundTicket hoặc thông tin tài khoản ngân hàng không hợp lệ");
        }

        String providerName = refundTicket.getBankAccount().getProviderName();
        String accountNumber = refundTicket.getBankAccount().getBankAccountNumber();
        String accountHolderName = refundTicket.getBankAccount().getAccountHolderName();

        String path = String.format(PATH_PATTERN, providerName, accountNumber);

        String encodedAccountName = URLEncoder.encode(accountHolderName, StandardCharsets.UTF_8);

        String url = String.format("%s/%s?%s=%s&%s=%.0f",
                BASE_URL,
                path,
                QUERY_PARAM_ACCOUNT_NAME,
                encodedAccountName,
                QUERY_PARAM_AMOUNT,
                refundTicket.getAmount());

        return url;
    }
}

