package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.payload.request.order.CheckOutModel;
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
        log.info("updateOrder() Start | orderId: {}, request: {}", orderId, request);
        OrderDTO dto = orderService.updateOrder(request, orderId);
        log.info("updateOrder() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy thông tin đơn hàng theo ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID orderId) {
        log.info("getOrderById() Start | orderId: {}", orderId);
        OrderDTO dto = orderService.getOrderById(orderId);
        log.info("getOrderById() End | id: {}", dto.getId());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy tất cả đơn hàng")
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@ParameterObject Pageable pageable, @RequestParam(name = "type", defaultValue = "unassigned") String type) {
        log.info("getAllOrders() Start");
        Page<OrderDTO> list = orderService.getAllOrders(pageable, type);
        log.info("getAllOrders() End | size: {}", list.getSize());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Xóa đơn hàng")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        log.info("deleteOrder() Start | orderId: {}", orderId);
        orderService.deleteOrder(orderId);
        log.info("deleteOrder() End");
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/checkout")
    @Operation(summary = "Thanh toán đơn hàng")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> checkoutOrder(@RequestBody CheckOutModel checkOutModel) {
        log.info("checkoutOrder() Start | subRequestId: {}", checkOutModel.getSubRequestId());
        OrderDTO orderDTO = orderService.checkoutOrder(checkOutModel);
        log.info("checkoutOrder() End | orderDTO: {}", orderDTO);
        return ResponseEntity.ok(orderDTO);
    }
}
