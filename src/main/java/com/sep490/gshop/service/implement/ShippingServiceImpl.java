package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.common.enums.DeliveryCode;
import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.external.shipping.ShippingTPS;
import com.sep490.gshop.external.shipping.ShippingTPSFactory;
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

    @Autowired
    public ShippingServiceImpl(ShippingTPSFactory shippingTPSFactory, OrderBusiness orderBusiness) {
        this.shippingTPSFactory = shippingTPSFactory;
        this.orderBusiness = orderBusiness;
    }


    @Override
    public String getShippingToken(DeliveryCode deliveryCode) {
        ShippingTPS shippingTPS = shippingTPSFactory.getService(deliveryCode);

        return shippingTPS.getShippingToken();
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
            orderBusiness.update(order);
            log.debug("Order status updated successfully for tracking number: {}", payload.getTrackingNumber());
            return new MessageResponse("Order status updated successfully for tracking number: " + payload.getTrackingNumber(), true);
        } catch (Exception e) {
            log.error("Error handling webhook for tracking number: {} | Error: {}", payload.getTrackingNumber(), e.getMessage());
            throw e;
        }
    }
}
