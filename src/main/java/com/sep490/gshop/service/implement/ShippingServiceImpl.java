package com.sep490.gshop.service.implement;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.entity.OrderHistory;
import com.sep490.gshop.entity.ShipmentTrackingEvent;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.external.shipping.ShippingTPSFactory;
import com.sep490.gshop.external.shipping.fedex.data.FedexWebhookEvent;
import com.sep490.gshop.payload.request.JSONStringInput;
import com.sep490.gshop.payload.request.shipment.ShipmentStatusRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.ShippingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ShippingServiceImpl implements ShippingService {

    private final ShippingTPSFactory shippingTPSFactory;
    private final OrderBusiness orderBusiness;
    private final SendNotiService sendNotiService;

    @Autowired
    public ShippingServiceImpl(ShippingTPSFactory shippingTPSFactory, OrderBusiness orderBusiness, SendNotiService sendNotiService) {
        this.shippingTPSFactory = shippingTPSFactory;
        this.orderBusiness = orderBusiness;
        this.sendNotiService = sendNotiService;
    }


    @Override
    public String getTrackingToken(DeliveryCode deliveryCode) {
        ShippingTPS shippingTPS = shippingTPSFactory.getService(deliveryCode);

        return shippingTPS.getTrackingToken();
    }

    @Override
    public String getShippingToken(DeliveryCode deliveryCode) {
        ShippingTPS shippingTPS = shippingTPSFactory.getService(deliveryCode);

        return shippingTPS.getShippingToken();
    }

    @Override
    public String getShippingRate(JSONStringInput inputJson) {
        try {
            log.debug("getShippingRate() ShippingServiceImpl Start");
            ShippingTPS shippingTPS = shippingTPSFactory.getService(DeliveryCode.FEDEX);
            String shippingRate = shippingTPS.getShippingRate(inputJson);
            log.debug("getShippingRate() ShippingServiceImpl End");
            return shippingRate;
        } catch (Exception e) {
            log.error("Error getting shipping rate: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String createShipment(JSONStringInput inputJson) {
        try {
            log.debug("createShipment() ShippingServiceImpl Start");
            ShippingTPS shippingTPS = shippingTPSFactory.getService(DeliveryCode.FEDEX);
            String shippingRate = shippingTPS.createShipment(inputJson);
            log.debug("createShipment() ShippingServiceImpl End");
            return shippingRate;
        } catch (Exception e) {
            log.error("createShipment() ShippingServiceImpl error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String tracking(String trackingNumber, DeliveryCode deliveryCode) {
        try {
            log.debug("tracking() TrackingServiceImpl Start");
            ShippingTPS shippingTPS = shippingTPSFactory.getService(deliveryCode);
            String trackingInfo = shippingTPS.tracking(trackingNumber);
            log.debug("tracking() TrackingServiceImpl End | trackingInfo: {}", trackingInfo);
            return trackingInfo;
        } catch (Exception e) {
            log.error("tracking() ShippingServiceImpl error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse handleFedexWebhook(String trackingNumber, FedexWebhookEvent request) {
        try {
            log.debug("handleFedexWebhook() ShippingServiceImpl Start | trackingNumber: {}", trackingNumber);
            Order order = orderBusiness.findByTrackingNumber(trackingNumber, DeliveryCode.FEDEX.getName().toLowerCase());
            if (order == null) {
                log.warn("handleFedexWebhook() ShippingServiceImpl warn | trackingNumber: {}", trackingNumber);
                return new MessageResponse("Không tìm thấy đơn hàng cho: " + trackingNumber, false);
            }
            ShippingTPS shippingTPS = shippingTPSFactory.getService(DeliveryCode.FEDEX);
            ShipmentTrackingEvent event = shippingTPS.webhookToShipmentTrackingEvent(request, trackingNumber);
            event.setOrder(order);
            order.getShipmentTrackingEvents().add(event);
            orderBusiness.update(order);
            BatchResponse response = sendNotiService.sendNotiToUser(order.getCustomer().getId(), "Cập nhật trạng thái đơn hàng", event.getEventDescription());
            log.debug("handleFedexWebhook() ShippingServiceImpl End | trackingNumber: {}", trackingNumber);
            return new MessageResponse("Order status updated successfully for tracking number: " + trackingNumber, true);
        } catch (FirebaseMessagingException e) {
            log.error("FirebaseMessagingException handling FedEx webhook for tracking number: {} | Error: {}", trackingNumber, e.getMessage());
            throw new AppException(500, "Lỗi gửi thông báo: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Error handling FedEx webhook for tracking number: {} | Error: {}", trackingNumber, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse handleWebhook(ShipmentStatusRequest payload) {
        try {
            log.debug("handleWebhook() ShippingServiceImpl Start | payload: {}", payload);
            Order order = orderBusiness.findByTrackingNumber(payload.getTrackingNumber(), payload.getDeliveryCode());
            if (order == null) {
                log.warn("Order not found for tracking number: {}", payload.getTrackingNumber());
                return new MessageResponse("Order not found for tracking number: " + payload.getTrackingNumber(),false);
            }
            log.debug("Order found: {}", order);
            if (order.getStatus().compareTo(payload.getStatus()) > 0) {
                String message = String.format("Không thể chuyển trạng thái từ %s sang %s", order.getStatus(), payload.getStatus());
                log.warn(message);
                return new MessageResponse(message, false);

            }
            order.setStatus(payload.getStatus());
            OrderHistory orderHistory = new OrderHistory(order, payload.getNote());
            order.getHistory().add(orderHistory);
            orderBusiness.update(order);
            log.debug("Order status updated successfully for tracking number: {}", payload.getTrackingNumber());
            return new MessageResponse("Order status updated successfully for tracking number: " + payload.getTrackingNumber(), true);
        } catch (Exception e) {
            log.error("Error handling webhook for tracking number: {} | Error: {}", payload.getTrackingNumber(), e.getMessage());
            throw e;
        }
    }
}
