package com.sep490.gshop.external.shipping.fedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("fedex")
public class FedExShippingTPS implements ShippingTPS {

    @Value("${fedex.url}")
    private String url;
    @Value("${fedex.apiKey}")
    private String apiKey;
    @Value("${fedex.secretKey}")
    private String secretKey;
    @Value("${fedex.accountNumber}")
    private String accountNumber;

    @Override
    public String getShippingToken() {
        try {
            OkHttpClient client = new OkHttpClient();

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

            Response response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new AppException(400, "Failed to fetch FedEx shipping token");
        }
    }

}
