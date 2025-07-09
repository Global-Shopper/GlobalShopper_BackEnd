package com.sep490.gshop.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
@Log4j2
public class VNPayUtils {
    @Value("${vnp.tmnCode}")
    private static String terminalCode;
    @Value("${vnp.hashSecret}")
    private static String vnpHasSecret;
    @Value("${vnp.url}")
    private static String vnpUrl;
    //@Value("${vnp.return-url}")
    private static String returnURL = "http://localhost:8080/api/wallet/check-payment-vnpay";



    public static String createURL(double money, String reason) {
        try {
            String currCode = "VND";
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", terminalCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", "test");
            vnpParams.put("vnp_OrderInfo", "Thanh toan: " + reason);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Amount", ((int) money) + "00");
            vnpParams.put("vnp_ReturnUrl", returnURL);
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

    public static Long getAmountFromReturnURL(String returnURL) {
        try {
            URL url = new URL(returnURL);
            String query = url.getQuery();
            Map<String, String> params = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf('=');
                    if (idx > 0 && idx < pair.length() - 1) {
                        String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                        params.put(key, value);
                    }
                }
            }
            String responseCode = params.get("vnp_ResponseCode");
            if (!"00".equals(responseCode)) {
                return null;
            }
            String amountStr = params.get("vnp_Amount");
            if (amountStr == null) return null;
            long amount = Long.parseLong(amountStr);
            return amount / 100;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
