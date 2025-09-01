package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.OrderBusiness;
import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.entity.OrderHistory;
import com.sep490.gshop.repository.OrderRepository;
import com.sep490.gshop.utils.TrackingMoreUtil;
import com.trackingmore.model.tracking.Tracking;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class OrderBusinessImpl extends BaseBusinessImpl<Order, OrderRepository> implements OrderBusiness {

    private final TrackingMoreUtil trackingMoreUtil;

    protected OrderBusinessImpl(OrderRepository repository, TrackingMoreUtil trackingMoreUtil) {
        super(repository);
        this.trackingMoreUtil = trackingMoreUtil;
    }

    @Override
    public Page<Order> getOrdersByCustomerId(UUID id, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return repository.findByCustomerIdAndStatus(id, status, pageable);
        }
        return repository.findByCustomerId(id, pageable);
    }

    @Override
    public Order getOrderByAdmin(UUID id) {
        return repository.getOrderByAdminId(id);
    }

    @Override
    public Page<Order> getAssignedOrdersByAdminId(UUID id, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return repository.findByAdminIdAndStatus(id, status, pageable);
        }
        return repository.findByAdminId(id, pageable);
    }

    @Override
    public Order findByTrackingNumber(String trackingNumber, String deliveryCode) {
        return repository.findByTrackingNumberAndShippingCarrier(trackingNumber, deliveryCode.toLowerCase());
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void updateShippingStatusCron() {
        try {
            log.info("updateShippingStatusCron() OrderBusinessImpl Start");
            List<Order> orders = repository.findByTypeAndStatusIsNotAndTrackingNumberIsNotNullAndShippingCarrierIsNotNull(RequestType.ONLINE, OrderStatus.DELIVERED);
            orders.forEach(order -> {
                Tracking tracking = trackingMoreUtil.getTracking(order.getShippingCarrier(), order.getTrackingNumber());
                if (tracking != null) {
                    OrderStatus newStatus = TrackingMoreUtil.mapToOrderStatus(tracking.getDeliveryStatus(), tracking.getSubstatus(), tracking.getDestinationCity());
                    order.setStatus(newStatus);
                    OrderHistory history = new OrderHistory(order, newStatus.getDescription());
                    order.getHistory().add(history);
                    repository.save(order);
                    log.info("Order {} status updated", order.getOrderCode());
                }
            });
            log.info("updateShippingStatusCron() OrderBusinessImpl End");
        } catch (Exception e) {
            log.error("Error in job update order status: {}", e.getMessage());
        }
    }
}
