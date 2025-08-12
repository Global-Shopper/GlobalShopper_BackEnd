package com.sep490.gshop.external.shipping.fedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.gshop.config.handler.AppException;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class FedExAuthService {
    @Value("${fedex.url}")
    private String url;
    @Value("${fedex.apiKey}")
    private String apiKey;
    @Value("${fedex.secretKey}")
    private String secretKey;
    @Value("${fedex.accountNumber}")
    private String accountNumber;
    @Value("${fedex.ship.apiKey}")
    private String shipApiKey;
    @Value("${fedex.ship.secretKey}")
    private String shipSecretKey;

    private final OkHttpClient http;          // inject bean OkHttpClient
    private final ObjectMapper mapper;

    @Autowired
    public FedExAuthService(ObjectMapper mapper) {
        this.http = new OkHttpClient();
        this.mapper = mapper;
    }


    @Cacheable("trackingToken")
    public String getTrackingToken() {
        log.info("getTrackingToken() - Start");
        return getString(apiKey, secretKey);
    }

    @Cacheable("shippingToken")
    public String getShippingToken() {
        log.info("getShippingToken() - Start");
        return getString(shipApiKey, shipSecretKey);
    }

    @CacheEvict(value = {"trackingToken", "shippingToken"}, allEntries = true)
    public void clearFedExTokenCache() {
        // Không cần code gì, annotation sẽ xóa cache
    }

    private String getString(String apiKey, String secretKey) {
        try {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

            String input = "grant_type=client_credentials"
                    + "&client_id=" + apiKey
                    + "&client_secret=" + secretKey;

            RequestBody body = RequestBody.create(mediaType, input);

            Request request = new Request.Builder()
                    .url(url + "/oauth/token")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            Response response = http.newCall(request).execute();
            JsonNode root = mapper.readTree(response.body().string());
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new AppException(400, "Failed to fetch FedEx shipping token");
        }
    }
}
