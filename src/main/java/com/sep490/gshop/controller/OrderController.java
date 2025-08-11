package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.CancelModel;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.payload.request.order.CheckOutModel;
import com.sep490.gshop.payload.request.order.DirectCheckoutModel;
import com.sep490.gshop.payload.request.order.ShippingInformationModel;
import com.sep490.gshop.payload.response.PaymentURLResponse;
import com.sep490.gshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.ORDER)
@Log4j2
public class OrderController {

    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Tạo mới đơn hàng")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest request) {
        log.info("createOrder() OrderController Start | request: {}", request);
        OrderDTO dto = orderService.createOrder(request);
        log.info("createOrder() OrderController End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Cập nhật đơn hàng")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderRequest request) {
        log.info("updateOrder() OrderController Start | orderId: {}, request: {}", orderId, request);
        OrderDTO dto = orderService.updateOrder(request, orderId);
        log.info("updateOrder() OrderController End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy thông tin đơn hàng theo ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID orderId) {
        log.info("getOrderById() OrderController Start | orderId: {}", orderId);
        OrderDTO dto = orderService.getOrderById(orderId);
        log.info("getOrderById() OrderController End | id: {}", dto.getId());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy tất cả đơn hàng")
    @PageableAsQueryParam
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@ParameterObject Pageable pageable,
                                                       @RequestParam(required = false) OrderStatus status) {
        log.info("getAllOrders() OrderController Start");
        Page<OrderDTO> list = orderService.getAllOrders(pageable, status);
        log.info("getAllOrders() OrderController End | size: {}", list.getSize());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Xóa đơn hàng")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        log.info("deleteOrder() OrderController Start | orderId: {}", orderId);
        orderService.deleteOrder(orderId);
        log.info("deleteOrder() OrderController End");
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/checkout")
    @Operation(summary = "Thanh toán đơn hàng")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> checkoutOrder(@RequestBody CheckOutModel checkOutModel) {
        log.info("checkoutOrder() OrderController Start | subRequestId: {}", checkOutModel.getSubRequestId());
        OrderDTO orderDTO = orderService.checkoutOrder(checkOutModel);
        log.info("checkoutOrder() OrderController End | orderDTO: {}", orderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/direct-checkout")
    @Operation(summary = "Thanh toán đơn hàng")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentURLResponse> directCheckoutOrder(@RequestBody DirectCheckoutModel checkOutModel) {
        log.info("directCheckoutOrder() OrderController Start | subRequestId: {}", checkOutModel.getSubRequestId());
        PaymentURLResponse response = orderService.directCheckoutOrder(checkOutModel);
        log.info("directCheckoutOrder() OrderController End | orderDTO: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-shipping/{orderId}")
    @Operation(summary = "Cập nhật thông tin vận chuyển của đơn hàng")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateShippingInfo(@PathVariable String orderId, @RequestBody ShippingInformationModel shippingInformationModel) {
        log.info("updateShippingInfo() OrderController Start | orderId: {}, shippingInformationModel: {}", orderId, shippingInformationModel);
        OrderDTO updatedOrder = orderService.updateShippingInfo(orderId, shippingInformationModel);
        log.info("updateShippingInfo() OrderController End | updatedOrder: {}", updatedOrder);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/cancel/{orderId}")
    @Operation(summary = "Hủy đơn hàng do không mua được sản phẩm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable UUID orderId, @RequestBody CancelModel cancelModel) {
        log.info("cancelOrder() OrderController Start | orderId: {}", orderId);
        OrderDTO cancelledOrder = orderService.cancelOrder(orderId, cancelModel);
        log.info("cancelOrder() OrderController End | cancelledOrder: {}", cancelledOrder);
        return ResponseEntity.ok(cancelledOrder);
    }
}
