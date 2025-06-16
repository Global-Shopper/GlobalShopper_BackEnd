package com.sep490.gshop.controller;

import com.sep490.gshop.common.URLConstant;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(URLConstant.ORDER)
@Log4j2
public class OrderController {

    private  OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Tạo mới đơn hàng")
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequest request) {
        log.debug("createOrder() Start | request: {}", request);
        OrderDTO dto = orderService.createOrder(request);
        log.debug("createOrder() End | dto: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Cập nhật đơn hàng")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderRequest request) {
        log.debug("updateOrder() Start | orderId: {}, request: {}", orderId, request);
        OrderDTO dto = orderService.updateOrder(request, orderId);
        log.debug("updateOrder() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy thông tin đơn hàng theo ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID orderId) {
        log.debug("getOrderById() Start | orderId: {}", orderId);
        OrderDTO dto = orderService.getOrderById(orderId);
        log.debug("getOrderById() End | dto: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Lấy tất cả đơn hàng")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        log.debug("getAllOrders() Start");
        List<OrderDTO> list = orderService.getAllOrders();
        log.debug("getAllOrders() End | size: {}", list.size());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Xóa đơn hàng")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        log.debug("deleteOrder() Start | orderId: {}", orderId);
        orderService.deleteOrder(orderId);
        log.debug("deleteOrder() End");
        return ResponseEntity.noContent().build();
    }
}
