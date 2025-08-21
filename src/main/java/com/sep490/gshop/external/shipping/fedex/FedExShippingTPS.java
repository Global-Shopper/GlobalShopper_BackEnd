package com.sep490.gshop.external.shipping.fedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sep490.gshop.common.enums.ShipmentStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.ShipmentTrackingEvent;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.external.shipping.fedex.data.FedexWebhookEvent;
import com.sep490.gshop.payload.request.JSONStringInput;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;

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
        } catch (Exception e) {
            throw new AppException(400, e.getMessage());
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

    @Override
    public String tracking(String trackingNumber) {
        try {
            JsonNode input = createTrackingInput(trackingNumber);
            OkHttpClient client = new OkHttpClient();
            String token = fedExAuthService.getTrackingToken();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, input.toString());
            Request request = new Request.Builder()
                    .url(url + "/track/v1/trackingnumbers")
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

    @Override
    public ShipmentTrackingEvent webhookToShipmentTrackingEvent(FedexWebhookEvent request, String trackingNumber) {
        if (request == null || request.getScanEvent() == null) {
            return null;
        }

        FedexWebhookEvent.ScanEvent scan = request.getScanEvent();

        // Lấy địa chỉ nếu có
        FedexWebhookEvent.Address addr = null;
        if (scan.getLocation() != null &&
                scan.getLocation().getLocationContactAndAddress() != null) {
            addr = scan.getLocation()
                    .getLocationContactAndAddress()
                    .getAddress();
        }

        // Build entity
        return ShipmentTrackingEvent.builder()
                .trackingNumber(trackingNumber)
                .eventCode(scan.getEventCode())
                .eventDescription(scan.getEventDescription())
                .eventTime(toEpochMilli(scan.getEventCreateTime()))
                .city(addr != null ? addr.getCity() : null)
                .country(addr != null ? addr.getCountryCode() : null)
                .shipmentStatus(mapToShipmentStatus(scan.getEventCode()))
                .build();
    }

    private JsonNode createTrackingInput(String trackingNumber) {
        ObjectMapper mapper = new ObjectMapper();

        // Root object
        ObjectNode root = mapper.createObjectNode();

        // trackingInfo array
        ArrayNode trackingInfoArray = mapper.createArrayNode();

        // trackingNumberInfo object
        ObjectNode trackingNumberInfo = mapper.createObjectNode();
        trackingNumberInfo.put("trackingNumber", trackingNumber);

        // trackingInfo item
        ObjectNode trackingInfoItem = mapper.createObjectNode();
        trackingInfoItem.set("trackingNumberInfo", trackingNumberInfo);

        // Add item to trackingInfo array
        trackingInfoArray.add(trackingInfoItem);

        // Put trackingInfo into root
        root.set("trackingInfo", trackingInfoArray);

        // includeDetailedScans field
        root.put("includeDetailedScans", true);
        return root;
    }

    private static ShipmentStatus mapToShipmentStatus(String eventCode) {
        if (eventCode == null) return ShipmentStatus.IN_TRANSIT;

        switch (eventCode) {
            // ---- CREATED (đơn mới lên, chưa pickup) ----
            case "OC": // Shipment info sent to FedEx
            case "HP": // Ready for pickup
            case "US": // Scheduled delivery updated (chỉ thay đổi plan, chưa chạy)
            case "LC": // Return label link cancelled
            case "RD": // Return label expired
            case "RG": // Return label expiring soon
            case "RP": // Return label emailed
            case "RS": // Returning package to shipper
            case "CA": // Shipment cancelled
                return ShipmentStatus.CREATED;

            // ---- PICKED UP (FedEx đã nhận hàng) ----
            case "DO": // Dropped Off
            case "DS": // Vehicle dispatched
            case "PD": // Pickup delayed
            case "PU": // Picked up
            case "IP": // In FedEx possession
                return ShipmentStatus.PICKED_UP;

            // ---- IN TRANSIT (hàng đang di chuyển) ----
            case "IT": // On the way
            case "DP": // Left origin facility
            case "TR": // Enroute to delivery
            case "PM": // In progress
            case "MD": // Manifest data
            case "DR": // Vehicle furnished not used
            case "OX": // Handed to USPS
            case "EA": // US Export Approved
            case "IN": // On Demand Care completed
            case "CH": // Location changed
            case "AO": // Arriving on time
            case "AE": // Arriving early
            case "DY": // Delivery updated
                return ShipmentStatus.IN_TRANSIT;

            // ---- ARRIVED DESTINATION (tới kho/cảng nước đến hoặc thông quan) ----
            case "AR": // Arrived at port of entry
            case "AF": // At local facility
            case "AC": // At Canada Post
            case "CP": // Clearance in progress
            case "CC": // International shipment release
                return ShipmentStatus.ARRIVED_IN_DESTINATION;

            // ---- DELIVERED (đã phát) ----
            case "OD": // Out for delivery
            case "DL": // Delivered
                return ShipmentStatus.DELIVERED;

            // ---- EXCEPTION (các lỗi / ngoại lệ) ----
            case "DD": // Delivery Delay
            case "DE": // Delivery Exception
            case "SE": // Shipment Exception
            case "CD": // Clearance delay
            case "RR": // Delivery option requested
            case "RM": // Delivery option updated
            case "HA": // Hold at location accepted
            case "RT": // Return to shipper requested
            case "RA": // Address change requested
            case "PR": // Redirect completed
            case "AS": // Address corrected
                return ShipmentStatus.EXCEPTION;

            // ---- FALLBACK ----
            default:
                return ShipmentStatus.IN_TRANSIT;
        }
    }

    private Long toEpochMilli(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toInstant().toEpochMilli() : null;
    }

}
