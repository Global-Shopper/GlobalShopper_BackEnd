package com.sep490.gshop.external.shipping.fedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.payload.request.JSONStringInput;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("fedex")
@Log4j2
public class FedExShippingTPS implements ShippingTPS {

    @Value("${fedex.url}")
    private String url;

    private final FedExAuthService fedExAuthService;

    @Autowired
    public FedExShippingTPS(FedExAuthService fedExAuthService) {
        this.fedExAuthService = fedExAuthService;
    }


    @Override
    public String getTrackingToken() {
        return fedExAuthService.getTrackingToken();
    }
    @Override
    public String getShippingToken() {
        return fedExAuthService.getShippingToken();
    }


    @Override
    public String getShippingRate(JSONStringInput inputJson) {
        try {
            OkHttpClient client = new OkHttpClient();
            String token = fedExAuthService.getShippingToken();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, inputJson.getInputJson().toString());
            Request request = new Request.Builder()
                    .url(url + "/rate/v1/rates/quotes")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-locale", "en_US")
                    .addHeader("Authorization", "Bearer "+ token)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new AppException(400, "Failed to fetch FedEx shipping rate");
        }
    }

    @Override
    public String createShipment(JSONStringInput inputJson) {
        try {
            OkHttpClient client = new OkHttpClient();
            String token = fedExAuthService.getShippingToken();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, inputJson.getInputJson().toString());
            Request request = new Request.Builder()
                    .url(url + "/ship/v1/shipments")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-locale", "en_US")
                    .addHeader("Authorization", "Bearer "+ token)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new AppException(400, e.getMessage());
        }
    }
}
