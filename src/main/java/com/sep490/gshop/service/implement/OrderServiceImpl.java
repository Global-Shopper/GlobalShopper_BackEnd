package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.*;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.services.UserDetailsImpl;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import com.sep490.gshop.payload.dto.FeedbackDTO;
import com.sep490.gshop.payload.dto.OrderDTO;
import com.sep490.gshop.payload.dto.TransactionDTO;
import com.sep490.gshop.payload.request.CancelModel;
import com.sep490.gshop.payload.request.OrderRequest;
import com.sep490.gshop.payload.request.order.CheckOutModel;
import com.sep490.gshop.payload.request.order.DirectCheckoutModel;
import com.sep490.gshop.payload.request.order.RePayModel;
import com.sep490.gshop.payload.request.order.ShippingInformationModel;
import com.sep490.gshop.payload.response.PaymentURLResponse;
import com.sep490.gshop.service.OrderService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.RandomUtil;
import com.sep490.gshop.utils.TrackingMoreUtil;
import com.trackingmore.model.tracking.Tracking;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderBusiness orderBusiness;
    private final ModelMapper modelMapper;
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final ProductBusiness productBusiness;
    private final PurchaseRequestBusiness purchaseRequestBusiness;
    private final RequestItemBusiness requestItemBusiness;
    private final SubRequestBusiness subRequestBusiness;
    private final WalletBusiness walletBusiness;
    private final TransactionBusiness transactionBusiness;
    private final UserBusiness userBusiness;
    private final FeedbackBusiness feedbackBusiness;
    private final VNPayServiceImpl vNPayServiceImpl;
    private final TrackingMoreUtil trackingMoreUtil;

    @Autowired
    public OrderServiceImpl(OrderBusiness orderBusiness,
                            ModelMapper modelMapper,
                            ShippingAddressBusiness shippingAddressBusiness,
                            ProductBusiness productBusiness,
                            PurchaseRequestBusiness purchaseRequestBusiness,
                            RequestItemBusiness requestItemBusiness,
                            SubRequestBusiness subRequestBusiness,
                            WalletBusiness walletBusiness,
                            TransactionBusiness transactionBusiness,
                            UserBusiness userBusiness,
                            FeedbackBusiness feedbackBusiness, VNPayServiceImpl vNPayServiceImpl, TrackingMoreUtil trackingMoreUtil) {
        this.orderBusiness = orderBusiness;
        this.modelMapper = modelMapper;
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.productBusiness = productBusiness;
        this.purchaseRequestBusiness = purchaseRequestBusiness;
        this.requestItemBusiness = requestItemBusiness;
        this.subRequestBusiness = subRequestBusiness;
        this.walletBusiness = walletBusiness;
        this.transactionBusiness = transactionBusiness;
        this.userBusiness = userBusiness;
        this.feedbackBusiness = feedbackBusiness;
        this.vNPayServiceImpl = vNPayServiceImpl;
        this.trackingMoreUtil = trackingMoreUtil;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderRequest orderRequest) {
        log.debug("createOrder() OrderServiceImpl Start | request: {}", orderRequest);
        try {
            ShippingAddress shippingAddress = shippingAddressBusiness.getById(UUID.fromString(orderRequest.getShippingAddressId()))
                    .orElseThrow(() -> new AppException(404, "Shipping address not found"));
            AddressSnapshot addressSnapshot = new AddressSnapshot(shippingAddress);

            var order = modelMapper.map(orderRequest, Order.class);
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.ORDER_REQUESTED);
            order.setCustomer(null);
            order.setShippingAddress(addressSnapshot);

            List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                    .map(itemRequest -> {
                        Product product = productBusiness.getById(UUID.fromString(itemRequest.getProductId()))
                                .orElseThrow(() -> new AppException(404, "Product not found"));

                        OrderItem item = new OrderItem();
                        item.setOrder(order);
                        return item;
                    }).collect(Collectors.toList());
            order.setOrderItems(orderItems);
            var totalPrice = 0.0;
            for(OrderItem orderItem : orderItems) {
                totalPrice += 0.0;
            }
            order.setTotalPrice(totalPrice);
            log.debug("createOrder() OrderServiceImpl End | orderId: {}", order.getId());
            return modelMapper.map(orderBusiness.create(order), OrderDTO.class);
        } catch (Exception e) {
            log.error("createOrder() OrderServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(OrderRequest orderRequest, UUID orderId) {
        log.debug("updateOrder() OrderServiceImpl Start | orderId: {}, request: {}", orderId, orderRequest);
        try {
            Order existingOrder = orderBusiness.getById(orderId)
                    .orElseThrow(() -> new AppException(404, "Order not found"));

            existingOrder.setStatus(OrderStatus.ARRIVED_IN_DESTINATION);

            Order updatedOrder = orderBusiness.update(existingOrder);
            log.debug("updateOrder() OrderServiceImpl End | orderId: {}", orderId);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        } catch (Exception e) {
            log.error("updateOrder() OrderServiceImpl Exception | orderId: {}, message: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        log.debug("getOrderById() OrderServiceImpl Start | orderId: {}", orderId);
        try {

            UserDetailsImpl userDetails = AuthUtils.getCurrentUser();

            Order order = orderBusiness.getById(orderId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy đơn hàng"));
            if (UserRole.CUSTOMER.equals(userDetails.getRole()) && !order.getCustomer().getId().equals(userDetails.getId())) {
                log.error("getOrderById() Unauthorized access | orderId: {}, userId: {}, role: {}", orderId, userDetails.getId(), userDetails.getRole());
                throw new AppException(403, "Bạn không có quyền truy cập vào đơn hàng này");
            }
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            Feedback feedback = feedbackBusiness.getByOrderId(orderId);
            if (feedback != null) {
                FeedbackDTO feedbackDTO = modelMapper.map(feedback, FeedbackDTO.class);
                orderDTO.setFeedback(feedbackDTO);
            }
            log.debug("getOrderById() OrderServiceImpl End | orderId: {}", orderId);
            return orderDTO;
        } catch (Exception e) {
            log.error("getOrderById() OrderServiceImpl Exception | orderId: {}, message: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable, OrderStatus status) {
        log.debug("getAllOrders() OrderServiceImpl Start");
        try {
            UserDetailsImpl userDetails = AuthUtils.getCurrentUser();
            if (UserRole.CUSTOMER.equals(userDetails.getRole())) {
                Page<Order> orders = orderBusiness.getOrdersByCustomerId(userDetails.getId(), status, pageable);
                log.debug("getAllOrders() OrderServiceImpl Customer End | size: {}", orders.getSize());
                return orders.map(order -> modelMapper.map(order, OrderDTO.class));
            } else if (UserRole.ADMIN.equals(userDetails.getRole())) {
                Page<Order> orders = orderBusiness.getAssignedOrdersByAdminId(userDetails.getId(), status, pageable);
                log.debug("getAllOrders() OrderServiceImpl Admin End | size: {}", orders.getSize());
                return orders.map(order -> modelMapper.map(order, OrderDTO.class));
            } else {
                log.error("getAllOrders() OrderServiceImpl Unauthorized access | userId: {}, role: {}", userDetails.getId(), userDetails.getRole());
                throw new AppException(403, "Bạn không có quyền truy cập vào danh sách đơn hàng");
            }
        } catch (Exception e) {
            log.error("getAllOrders() OrderServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteOrder(UUID orderId) {
        log.debug("deleteOrder() OrderServiceImpl Start | orderId: {}", orderId);
        try {
            orderBusiness.delete(orderId);
            return true;
        } catch (Exception e) {
            log.error("deleteOrder() OrderServiceImpl Exception | orderId: {}, message: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public OrderDTO checkoutOrder(CheckOutModel checkOutModel) {
        try {
            log.debug("checkoutOrder() OrderServiceImpl Start | subRequestId: {}", checkOutModel.getSubRequestId());
            UUID userId = AuthUtils.getCurrentUserId();
            UUID subRequestId = UUID.fromString(checkOutModel.getSubRequestId());
            Customer user = (Customer) userBusiness.getByUserId(userId);
            SubRequest subRequest = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu"));
            PurchaseRequest purchaseRequest = purchaseRequestBusiness.findPurchaseRequestBySubRequestId(subRequestId);
            if (purchaseRequest == null) {
                log.error("checkoutOrder() OrderServiceImpl PurchaseRequest not found | subRequestId: {}", subRequestId);
                throw new AppException(404, "Không tìm thấy yêu cầu mua hàng");
            }
            if (!purchaseRequest.getCustomer().getId().equals(user.getId())) {
                log.error("checkoutOrder() OrderServiceImpl Unauthorized | subRequestId: {}, userId: {}", subRequestId, userId);
                throw new AppException(403, "Bạn không có quyền thực hiện hành động này");
            }

            if (subRequest.getQuotation() == null) {
                log.error("checkoutOrder() OrderServiceImpl Invalid PurchaseRequest status | subRequestId: {}, status: {}", subRequestId, purchaseRequest.getStatus());
                throw new AppException(400, "Yêu cầu mua hàng chưa được báo giá");
            }
            Order order = Order.builder()
                    .status(OrderStatus.ORDER_REQUESTED)
                    .customer(user)
                    .shippingAddress(purchaseRequest.getShippingAddress())
                    .admin(purchaseRequest.getAdmin())
                    .contactInfo(subRequest.getContactInfo())
                    .seller(subRequest.getSeller())
                    .currency(subRequest.getQuotation().getCurrency())
                    .region(subRequest.getQuotation().getRegion())
                    .ecommercePlatform(subRequest.getEcommercePlatform())
                    .totalPrice(subRequest.getQuotation().getTotalPriceEstimate())
                    .shippingFee(subRequest.getQuotation().getShippingEstimate())
                    .shippingCarrier(checkOutModel.getShippingCarrier())
                    .type(purchaseRequest.getRequestType())
                    .build();
            if (checkOutModel.getTrackingNumber() != null && checkOutModel.getShippingFee() != null) {
                order.setShippingFee(checkOutModel.getShippingFee());
                order.setTrackingNumber(checkOutModel.getTrackingNumber());
            }
            List<OrderItem> orderItems = subRequest.getRequestItems().stream()
                    .map(requestItem -> {
                        OrderItem orderItem = new OrderItem(requestItem);
                        orderItem.setOrder(order);
                        orderItem.setTotalVNDPrice(requestItem.getQuotationDetail().getTotalVNDPrice());
                        return orderItem;
                    }).toList();
            order.setOrderItems(orderItems);
            //Assuming calculate total price based and shipping fee
            double totalPrice = order.getTotalPrice() + order.getShippingFee();

            if (totalPrice != checkOutModel.getTotalPriceEstimate()) {
                log.error("checkoutOrder() OrderServiceImpl Total price mismatch | subRequestId: {}, expected: {}, actual: {}", subRequestId, checkOutModel.getTotalPriceEstimate(), totalPrice);
                throw new AppException(400, "Tổng giá trị đơn hàng không khớp");
            }
            double balance = user.getWallet().getBalance();
            if (balance < totalPrice) {
                log.error("checkoutOrder() OrderServiceImpl Insufficient wallet balance | userId: {}, required: {}, available: {}", userId, totalPrice, balance);
                throw new AppException(400, "Số dư ví không đủ để thanh toán");
            }
            Wallet wallet =  walletBusiness.checkoutOrder(totalPrice, user.getWallet(), UUID.fromString(checkOutModel.getSubRequestId()));
            if (wallet == null || wallet.getBalance() != balance - totalPrice) {
                Transaction transaction = transactionBusiness.getTransactionByReferenceCode(checkOutModel.getSubRequestId());
                transaction.setStatus(TransactionStatus.FAIL);
                transactionBusiness.update(transaction);
                log.error("checkoutOrder() OrderServiceImpl Wallet checkout failed | userId: {}", userId);
                throw new AppException(500, "Thanh toán không thành công");
            }

            // Update purchase request status and history
            purchaseRequest.setStatus(PurchaseRequestStatus.PAID);
            PurchaseRequestHistory purchaseRequestHistory = new PurchaseRequestHistory(purchaseRequest, "Yêu cầu mua hàng đã được xác nhận");
            purchaseRequest.getHistory().add(purchaseRequestHistory);
            purchaseRequestBusiness.update(purchaseRequest);

            // Update sub request status
            subRequest.setStatus(SubRequestStatus.PAID);
            subRequestBusiness.update(subRequest);

            // Create order
            OrderHistory history = new OrderHistory(order,"Đơn hàng đã được tạo");
            order.setHistory(List.of(history));
            OrderDTO res = modelMapper.map(orderBusiness.create(order), OrderDTO.class);
            log.debug("checkoutOrder() OrderServiceImpl End | orderId: {}", order.getId());
            return res;
        } catch (Exception e) {
            log.error("checkoutOrder() OrderServiceImpl Exception | subRequestId: {}, message: {}", checkOutModel.getSubRequestId(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public PaymentURLResponse directCheckoutOrder(DirectCheckoutModel checkOutModel) {
        try {
            UUID userId = AuthUtils.getCurrentUserId();
            UUID subRequestId = UUID.fromString(checkOutModel.getSubRequestId());
            Customer user = (Customer) userBusiness.getByUserId(userId);
            SubRequest subRequest = subRequestBusiness.getById(subRequestId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu"));
            PurchaseRequest purchaseRequest = purchaseRequestBusiness.findPurchaseRequestBySubRequestId(subRequestId);
            if (purchaseRequest == null) {
                log.error("directCheckoutOrder() OrderServiceImpl PurchaseRequest not found | subRequestId: {}", subRequestId);
                throw new AppException(404, "Không tìm thấy yêu cầu mua hàng");
            }
            if (!purchaseRequest.getCustomer().getId().equals(user.getId())) {
                log.error("directCheckoutOrder() OrderServiceImpl Unauthorized | subRequestId: {}, userId: {}", subRequestId, userId);
                throw new AppException(403, "Bạn không có quyền thực hiện hành động này");
            }

            if (subRequest.getQuotation() == null) {
                log.error("directCheckoutOrder() OrderServiceImpl Invalid PurchaseRequest status | subRequestId: {}, status: {}", subRequestId, purchaseRequest.getStatus());
                throw new AppException(400, "Yêu cầu mua hàng chưa được báo giá");
            }

            Order order = Order.builder()
                    .status(OrderStatus.AWAITING_PAYMENT)
                    .customer(user)
                    .shippingAddress(purchaseRequest.getShippingAddress())
                    .admin(purchaseRequest.getAdmin())
                    .contactInfo(subRequest.getContactInfo())
                    .seller(subRequest.getSeller())
                    .currency(subRequest.getQuotation().getCurrency())
                    .region(subRequest.getQuotation().getRegion())
                    .ecommercePlatform(subRequest.getEcommercePlatform())
                    .totalPrice(subRequest.getQuotation().getTotalPriceEstimate())
                    .shippingFee(subRequest.getQuotation().getShippingEstimate())
                    .type(purchaseRequest.getRequestType())
                    .shippingCarrier(checkOutModel.getShippingCarrier())
                    .build();
            if (checkOutModel.getTrackingNumber() != null && checkOutModel.getShippingFee() != null) {
                order.setShippingFee(checkOutModel.getShippingFee());
                order.setTrackingNumber(checkOutModel.getTrackingNumber());
            }
            List<OrderItem> orderItems = subRequest.getRequestItems().stream()
                    .map(requestItem -> {
                        OrderItem orderItem = new OrderItem(requestItem);
                        orderItem.setOrder(order);
                        return orderItem;
                    }).toList();
            order.setOrderItems(orderItems);
            //Assuming calculate total price based and shipping fee
            double totalPrice = order.getTotalPrice() + order.getShippingFee();

            if (totalPrice != checkOutModel.getTotalPriceEstimate()) {
                log.error("directCheckoutOrder() OrderServiceImpl Total price mismatch | subRequestId: {}, expected: {}, actual: {}", subRequestId, checkOutModel.getTotalPriceEstimate(), totalPrice);
                throw new AppException(400, "Tổng giá trị đơn hàng không khớp");
            }

            OrderHistory history = new OrderHistory(order, "Đơn hàng đã được tạo");
            order.setHistory(List.of(history));
            
            Order createdOrder = orderBusiness.create(order);
            String referenceCode = createdOrder.getId().toString();
            Transaction transaction = Transaction.builder()
                    .type(TransactionType.CHECKOUT)
                    .status(TransactionStatus.PENDING)
                    .amount(totalPrice)
                    .referenceCode(referenceCode)
                    .description("Thanh toán đơn hàng " + createdOrder.getId())
                    .customer(user)
                    .balanceAfter(user.getWallet().getBalance())
                    .balanceBefore(user.getWallet().getBalance())
                    .build();
            transactionBusiness.create(transaction);
            String url = vNPayServiceImpl.createURL(totalPrice, "Thanh toán đơn hàng", user.getEmail(),referenceCode,checkOutModel.getRedirectUri().replace(":id", createdOrder.getId().toString()));
            if (url == null || url.isBlank()) {
                log.error("directCheckoutOrder() OrderServiceImpl VNPay URL creation failed | subRequestId: {}", checkOutModel.getSubRequestId());
                throw new AppException(500, "Không thể tạo URL thanh toán");
            }
            purchaseRequest.setStatus(PurchaseRequestStatus.PAID);
            PurchaseRequestHistory purchaseRequestHistory = new PurchaseRequestHistory(purchaseRequest, "Yêu cầu mua hàng đã được xác nhận");
            purchaseRequest.getHistory().add(purchaseRequestHistory);
            purchaseRequestBusiness.update(purchaseRequest);
            subRequest.setStatus(SubRequestStatus.PAID);
            subRequestBusiness.update(subRequest);
            PaymentURLResponse response = PaymentURLResponse.builder()
                    .url(url)
                    .message("Vui lòng thanh toán để hoàn tất đơn hàng")
                    .isSuccess(true)
                    .build();
            log.debug("directCheckoutOrder() OrderServiceImpl End | subRequestId: {}, orderId: {}", checkOutModel.getSubRequestId(), createdOrder.getId());
            return response;
        } catch (Exception e) {
            log.error("directCheckoutOrder() OrderServiceImpl Exception | subRequestId: {}, message: {}", checkOutModel.getSubRequestId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public OrderDTO updateShippingInfo(String orderId, ShippingInformationModel shippingInformationModel) {
        log.debug("updateShippingInfo() Start | orderId: {}, shippingInformationModel: {}", orderId, shippingInformationModel);
        try {
            Order order = orderBusiness.getById(UUID.fromString(orderId))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy đơn hàng"));

            if (order.getStatus() != OrderStatus.ORDER_REQUESTED) {
                log.error("updateShippingInfo() Invalid order status | orderId: {}, status: {}", orderId, order.getStatus());
                throw new AppException(400, "Không thể cập nhật thông tin vận chuyển cho đơn hàng này");
            }
            order.setShippingCarrier(shippingInformationModel.getName());
            order.setOrderCode(shippingInformationModel.getOrderCode());
            order.setTrackingNumber(shippingInformationModel.getTrackingNumber());
            OrderHistory history = new OrderHistory(order,"Đơn hàng đã được mua");
            order.getHistory().add(history);
            Tracking tracking = trackingMoreUtil.getTracking(order.getShippingCarrier(), order.getTrackingNumber());
            if (tracking != null) {
                OrderStatus newStatus = TrackingMoreUtil.mapToOrderStatus(tracking.getDeliveryStatus(), tracking.getSubstatus(), tracking.getDestinationCity());
                order.setStatus(newStatus);
                OrderHistory trackingStatusHistory = new OrderHistory(order, newStatus.getDescription());
                order.getHistory().add(trackingStatusHistory);
                log.info("Order {} status updated", order.getOrderCode());
            }
            Order updatedOrder = orderBusiness.update(order);
            log.debug("updateShippingInfo() End | updatedOrder: {}", updatedOrder);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        } catch (Exception e) {
            log.error("updateShippingInfo() Exception | orderId: {}, message: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(UUID orderId, CancelModel cancelModel) {
        try {
            log.debug("cancelOrder() Start | orderId: {}, cancelModel: {}", orderId, cancelModel);
            Order order = orderBusiness.getById(orderId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy đơn hàng"));

            if (!OrderStatus.ORDER_REQUESTED.equals(order.getStatus())) {
                log.error("cancelOrder() Invalid order status | orderId: {}, status: {}", orderId, order.getStatus());
                throw new AppException(400, "Không thể hủy đơn hàng này, chỉ có thể hủy đơn hàng đang chờ xử lý");
            }
            order.setStatus(OrderStatus.CANCELLED);
            OrderHistory history = new OrderHistory(order, cancelModel.getRejectionReason());
            order.getHistory().add(history);
            Order updatedOrder = orderBusiness.update(order);
            Wallet wallet = order.getCustomer().getWallet();
            walletBusiness.addBalance(order.getTotalPrice()+ order.getShippingFee(), wallet, order.getId().toString(), "Hoàn tiền do hủy đơn hàng");
            log.debug("cancelOrder() End | updatedOrder: {}", updatedOrder);
            return modelMapper.map(updatedOrder, OrderDTO.class);
        } catch (Exception e) {
            log.error("cancelOrder() Exception | orderId: {}, message: {}", orderId, e.getMessage());
            throw e;
        }
    }

    @Override
    public PaymentURLResponse rePayOrder(RePayModel rePayModel) {
        try {
            Order order = orderBusiness.getById(UUID.fromString(rePayModel.getOrderId()))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy đơn hàng"));
            double totalPayment = order.getTotalPrice() + order.getShippingFee();
            Transaction transaction = transactionBusiness.getTransactionByReferenceCode(rePayModel.getOrderId());
            Customer customer = (Customer) userBusiness.getByUserId(AuthUtils.getCurrentUserId());
            if (!OrderStatus.AWAITING_PAYMENT.equals(order.getStatus())) {
                return PaymentURLResponse.builder()
                        .isSuccess(false)
                        .message("Chỉ có thể thanh toán lại cho đơn hàng đang chờ thanh toán")
                        .url(null)
                        .build();
            }
            if (rePayModel.getPaymentMethod().equals("wallet")) {
                if (customer.getWallet().getBalance() < totalPayment) {
                    return PaymentURLResponse.builder()
                            .isSuccess(false)
                            .message("Số dư ví không đủ để thanh toán")
                            .url(null)
                            .build();
                }
                Wallet wallet = walletBusiness.repayOrder(totalPayment, customer.getWallet(), UUID.fromString(rePayModel.getOrderId()), transaction);
                if (wallet == null) {
                    return PaymentURLResponse.builder()
                            .isSuccess(false)
                            .message("Thanh toán không thành công")
                            .url(null)
                            .build();
                }
                order.setStatus(OrderStatus.ORDER_REQUESTED);
                OrderHistory history = new OrderHistory(order, "Đơn hàng đã được thanh toán lại qua ví");
                order.getHistory().add(history);
                orderBusiness.update(order);
                return PaymentURLResponse.builder()
                        .isSuccess(true)
                        .message("Thanh toán đơn hàng thành công")
                        .url(null)
                        .build();
            } else {
                String url = vNPayServiceImpl.createURL(order.getTotalPrice() + order.getShippingFee(),
                        "Thanh toán lại đơn hàng",
                        order.getCustomer().getEmail(),
                        order.getId().toString(),
                        rePayModel.getRedirectUri().replace(":id", order.getId().toString()));
                if (url == null || url.isBlank()) {
                    log.error("rePayOrder() VNPay URL creation failed | orderId: {}", rePayModel.getOrderId());
                    return PaymentURLResponse.builder()
                            .isSuccess(false)
                            .message("Không thể tạo URL thanh toán")
                            .url(null)
                            .build();
                }
                return PaymentURLResponse.builder()
                        .isSuccess(true)
                        .message("Vui lòng thanh toán để hoàn tất đơn hàng")
                        .url(url)
                        .build();

            }
        } catch (Exception e) {
            log.error("rePayOrder() Exception | rePayModel: {}, message: {}", rePayModel, e.getMessage());
            throw e;
        }
    }
}