package com.sep490.gshop.external.shipping.fedex.data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FedexWebhookEvent {

    @JsonProperty("scanEvent")
    private ScanEvent scanEvent;

    @JsonProperty("trackResult")
    private TrackResult trackResult;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScanEvent {
        private String eventCode;               // trạng thái FedEx (IT, AR, DL, DP…)
        private String eventDescription;        // mô tả trạng thái
        private OffsetDateTime eventCreateTime; // thời gian xảy ra sự kiện
        private Location location;              // vị trí
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private LocationContactAndAddress locationContactAndAddress;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationContactAndAddress {
        private Address address;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String city;
        private String stateOrProvinceCode;
        private String postalCode;
        private String countryCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrackResult {
        private TrackingInfo trackingInfo;
        private EstimatedDeliveryDate estimatedDeliveryDate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrackingInfo {
        private CurrentTrackingInfo currentTrackingInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentTrackingInfo {
        private String trackingNumber; // số tracking để map với đơn hàng
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EstimatedDeliveryDate {
        private NewDate newDate;
        private String arrivalStatusCode; // ONTIME / DELAYED
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewDate {
        private OffsetDateTime begins; // thời gian giao hàng dự kiến
        private OffsetDateTime ends;
    }
}
