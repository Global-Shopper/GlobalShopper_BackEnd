package com.sep490.gshop.utils;

import com.sep490.gshop.common.enums.OrderStatus;
import com.trackingmore.TrackingMore;
import com.trackingmore.exception.TrackingMoreException;
import com.trackingmore.model.TrackingMoreResponse;
import com.trackingmore.model.tracking.CreateTrackingParams;
import com.trackingmore.model.tracking.GetTrackingResultsParams;
import com.trackingmore.model.tracking.Tracking;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Log4j2
public class TrackingMoreUtil {
    @Value("${trackingmore.apiKey}")
    private String apiKey;
    private TrackingMore client() throws TrackingMoreException {
        return new TrackingMore(apiKey);
    }

    public Tracking createTracking(String courierCode, String trackingNumber) {
        try {
            CreateTrackingParams params = new CreateTrackingParams();
            params.setCourierCode(courierCode);
            params.setTrackingNumber(trackingNumber);
            TrackingMoreResponse<Tracking> resp = client().trackings.CreateTracking(params);
            return resp.getData();
        } catch (TrackingMoreException | IOException e) {
            log.error("Create tracking failed: {}", e.getMessage());
            return null;
        }
    }

    public Tracking getTracking(String courierCode, String trackingNumber) {
        try {
            GetTrackingResultsParams params = new GetTrackingResultsParams();
            params.setCourierCode(courierCode.toLowerCase());
            params.setTrackingNumbers(trackingNumber);
            TrackingMoreResponse<List<Tracking>> resp = client().trackings.GetTrackingResults(params);
            if (resp.getMeta().getCode() != 200 ) {
                if (resp.getMeta().getCode() == 4102) {
                    return createTracking(courierCode, trackingNumber);
                }
                return null;
            }

            return resp.getData().isEmpty() ? null : resp.getData().get(0);
        } catch (TrackingMoreException | IOException e) {
            log.error("Get tracking failed: {}", e.getMessage());
            return null;
        }
    }

    public static OrderStatus mapToOrderStatus(String status, String subStatus, String destCode) {
        String s = (status == null) ? "" : status.trim().toLowerCase(Locale.ROOT);
        String sub = (subStatus == null) ? "" : subStatus.toLowerCase(Locale.ROOT);
        String dest = (destCode == null) ? "" : destCode.toUpperCase(Locale.ROOT);
        switch (s) {
            case "delivered" -> {
                return OrderStatus.DELIVERED;
            }
            case "pickup" -> {
                return OrderStatus.IN_TRANSIT;
            }
            case "transit" -> {
                if (containsArrivedAtDestination(sub) || "VN".equals(dest)) {
                    return OrderStatus.ARRIVED_IN_DESTINATION;
                }
                return OrderStatus.IN_TRANSIT;
            }
            case "undelivered", "exception", "expired" -> {
                return OrderStatus.CANCELLED;
            }
            case "pending", "inforeceived", "notfound" -> {
                return OrderStatus.PURCHASED;
            }
            default -> {
                return OrderStatus.PURCHASED;
            }
        }
    }
    private static boolean containsArrivedAtDestination(String text) {
        if (Objects.isNull(text) || text.isBlank()) return false;
        return text.contains("arrived at destination")
                || text.contains("arrived at delivery facility")
                || text.contains("arrival at inward office of exchange")
                || text.contains("arrived in destination")
                || text.contains("đến kho đích")
                || text.contains("đã đến nơi phát");
    }




}
