package com.sep490.gshop.service.implement;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Service
@Log4j2
public class VNPayServiceImpl {
    @Value("${vnp.tmnCode}")
    private String terminalCode;
    @Value("${vnp.hashSecret}")
    private String vnpHasSecret;
    @Value("${vnp.url}")
    private String vnpUrl;
    @Value("${vnp.return-url}")
    private String returnURL;



    public String createURL(double money, String reason, String userEmail) {
        try {
            var random = ThreadLocalRandom.current();
            String randomStr = random.toString();
            int atIndex = randomStr.indexOf('@');
            String txnRef = (atIndex != -1) ? randomStr.substring(atIndex + 1) : randomStr;
            String currCode = "VND";
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", terminalCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", reason + " số tiền: " + money);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Amount", ((int) money) + "00");
            String returnUrlWithEmail = returnURL + "/wallet/deposit" + "?email=" + URLEncoder.encode(userEmail, StandardCharsets.UTF_8.toString());
            vnpParams.put("vnp_ReturnUrl", returnUrlWithEmail);
            vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            vnpParams.put("vnp_IpAddr", "167.99.74.201");

            StringBuilder signDataBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("=");
                signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

            String signData = signDataBuilder.toString();
            String signed = generateHMAC(vnpHasSecret, signData);

            vnpParams.put("vnp_SecureHash", signed);

            StringBuilder urlBuilder = new StringBuilder(vnpUrl);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);

            String paymentUrl = urlBuilder.toString();
            String sercureHase = vnpParams.get("vnp_SecureHash");
            log.debug("createURL() Success | paymentUrl: {}", paymentUrl);
            return paymentUrl;

        } catch (UnsupportedEncodingException e) {
            log.error("createURL() Exception | message: {}", e.getMessage());
            return "";
        }
    }

    public static String generateHMAC(String secretKey, String signData) {
        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSha512.init(keySpec);
            byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : hmacBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("generateHMAC() Exception | message: {}", e.getMessage());
            return "";
        }
    }

}
