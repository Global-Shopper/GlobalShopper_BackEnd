package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.WithdrawStatus;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.dto.WithdrawTicketDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.request.WithdrawRequest;
import com.sep490.gshop.payload.response.CloudinaryResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MessageWithBankInformationResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import com.sep490.gshop.service.CloudinaryService;
import com.sep490.gshop.service.WalletService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {
    private final WalletBusiness walletBusiness;
    private final CustomerBusiness customerBusiness;
    private final ModelMapper modelMapper;
    private VNPayServiceImpl vnPayServiceImpl;
    private BankAccountBusiness bankAccountBusiness;
    private UserBusiness userBusiness;
    private WithdrawTicketBusiness withdrawTicketBusiness;
    private CloudinaryService cloudinaryService;

    @Autowired
    public WalletServiceImpl(WalletBusiness walletBusiness,
                             CustomerBusiness customerBusiness,
                             ModelMapper modelMapper,
                             VNPayServiceImpl vnPayServiceImpl,
                             BankAccountBusiness bankAccountBusiness,
                             UserBusiness userBusiness,
                             WithdrawTicketBusiness withdrawTicketBusiness,
                             CloudinaryService cloudinaryService) {
        this.walletBusiness = walletBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
        this.vnPayServiceImpl = vnPayServiceImpl;
        this.bankAccountBusiness = bankAccountBusiness;
        this.userBusiness = userBusiness;
        this.withdrawTicketBusiness = withdrawTicketBusiness;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public MoneyChargeResponse depositMoney(@Valid WalletRequest request) {
        log.debug("depositMoney() Start | request: {}", request);
        try {
            Customer customer = customerBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> AppException.builder()
                            .message("Bạn cần đăng nhập để sử dụng dịch vụ")
                            .code(401)
                            .build());

            Wallet wallet = walletBusiness.getById(customer.getWallet().getId())
                    .orElseThrow(() -> AppException.builder()
                            .code(404)
                            .message("Không tìm thấy ví của bạn")
                            .build());

            var url = vnPayServiceImpl.createURL(request.getBalance(),
                    "Nạp tiền vào tài khoản " + customer.getName(),
                    customer.getEmail());

            log.debug("depositMoney() End | url: {}", url);
            String formattedAmount = formatAmount(request.getBalance());
            return MoneyChargeResponse.builder()
                    .isSuccess(true)
                    .message("Đã yêu cầu nạp thành công số tiền " + formattedAmount + " VNĐ vào ví của bạn")
                    .url(url)
                    .build();

        } catch (Exception e) {
            log.error("depositMoney() Unexpected Exception | message: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public MessageResponse processVNPayReturn(String email, String status, String amount) {
        log.debug("processVNPayReturn() Start | email: {}", email);
        try {
            double amountGet = Double.parseDouble(amount);
            if (!status.equals("00")) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Thanh toán VNPay không thành công hoặc dữ liệu không hợp lệ")
                        .build();
            }
            Customer customer = customerBusiness.findByEmail(email);
            if(email == null || customer == null) {
                throw AppException.builder().message("Không tìm thấy người dùng, thử lại sau").code(404).build();
            }
            Wallet wallet = walletBusiness.getById(customer.getWallet().getId())
                    .orElseThrow(() -> AppException.builder()
                            .code(404)
                            .message("Không tìm thấy ví của bạn")
                            .build());
            wallet.setBalance(wallet.getBalance() + amountGet);
            walletBusiness.update(wallet);
            log.debug("processVNPayReturn() End | updated balance: {}", wallet.getBalance());


            String formattedAmount = formatAmount(amountGet);
            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Nạp tiền thành công: " + formattedAmount + " VNĐ vào ví")
                    .build();
        }catch (Exception e) {
            log.error("processVNPayReturn() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }


    private String formatAmount(double amount) {
        DecimalFormat df;
        if (amount == (long) amount) {
            df = new DecimalFormat("#,##0");
            return df.format(amount);
        } else {
            df = new DecimalFormat("#,##0.00");
            return df.format(amount);
        }
    }


    @Override
    public MessageResponse withdrawMoneyRequest(@Valid WithdrawRequest request) {
        log.debug("withdrawMoneyRequest() Start | request: {}", request);
        try {
            UUID currentUserId = AuthUtils.getCurrentUserId();
            if (currentUserId == null) {
                throw AppException.builder()
                        .message("Bạn cần đăng nhập để tiếp tục")
                        .code(401)
                        .build();
            }

            var currentUser = customerBusiness.getById(currentUserId)
                    .orElseThrow(() -> AppException.builder()
                            .message("User không tồn tại")
                            .code(404)
                            .build());

            Wallet wallet = walletBusiness.getById(currentUser.getWallet().getId())
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy ví của bạn"));

            if (request.getAmount() <= 0) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Số tiền rút phải lớn hơn 0")
                        .build();
            }

            if (wallet.getBalance() < request.getAmount()) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Số dư ví không đủ để thực hiện yêu cầu")
                        .build();
            }

            BankAccount bankAccount = bankAccountBusiness.getById(UUID.fromString(request.getBankAccountId()))
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy tài khoản ngân hàng nhận tiền"));

            BankAccountSnapshot bankAccountSnapshot = new BankAccountSnapshot(bankAccount);

            WithdrawTicket withdrawTicket = modelMapper.map(request, WithdrawTicket.class);
            withdrawTicket.setStatus(WithdrawStatus.PENDING);
            withdrawTicket.setBankAccount(bankAccountSnapshot);
            withdrawTicketBusiness.create(withdrawTicket);

            log.debug("withdrawMoneyRequest() End | rút tiền thành công, số tiền: {}", request.getAmount());

            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Yêu cầu rút tiền thành công, số tiền: " + request.getAmount())
                    .build();

        }catch (Exception e) {
            log.error("withdrawMoneyRequest() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }
    @Override
    public List<WithdrawTicketDTO> getWithdrawTicketsWithPendingStatus() {
        log.debug("getWithdrawTicketsWithPendingStatus() Start");
        try {
            List<WithdrawTicketDTO> tickets = withdrawTicketBusiness.findByStatus(WithdrawStatus.PENDING)
                    .stream().map(t -> modelMapper.map(t, WithdrawTicketDTO.class)).collect(Collectors.toList());
            log.debug("getWithdrawTicketsWithPendingStatus() End | found {} tickets", tickets.size());
            return tickets;
        } catch (Exception e) {
            log.error("getWithdrawTicketsWithPendingStatus() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public WalletDTO getWalletByCurrent() {
        log.debug("getWalletByCurrent() Start");
        try {
            Customer customer = customerBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> AppException.builder()
                            .message("Bạn cần đăng nhập để sử dụng dịch vụ")
                            .code(401)
                            .build());

            Wallet wallet = walletBusiness.getById(customer.getWallet().getId())
                    .orElseThrow(() -> AppException.builder()
                            .code(404)
                            .message("Không tìm thấy ví của bạn, hãy thử lại")
                            .build());

            WalletDTO walletDTO = modelMapper.map(wallet, WalletDTO.class);
            log.debug("getWalletByCurrent() End | result: {}", walletDTO);
            return walletDTO;

        } catch (Exception e) {
            log.error("getWalletByCurrent() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    public MessageWithBankInformationResponse processWithdrawRequest(UUID withdrawTicketId, boolean isApproved, String denyReason) {
        log.debug("processWithdrawRequest() Start | withdrawTicketId: {}", withdrawTicketId);
        try {
            WithdrawTicket withdrawTicket = withdrawTicketBusiness.getById(withdrawTicketId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu rút tiền"));


            if (withdrawTicket.getStatus() != WithdrawStatus.PENDING) {
                return MessageWithBankInformationResponse.builder()
                        .isSuccess(false)
                        .message("Yêu cầu này đã được xử lý trước đó")
                        .build();
            }

            if (isApproved) {
                withdrawTicket.setStatus(WithdrawStatus.APPROVED);
                withdrawTicketBusiness.update(withdrawTicket);
                log.debug("processWithdrawRequest() Rút tiền được chấp nhận | withdrawTicketId: {}", withdrawTicketId);
                return MessageWithBankInformationResponse.builder()
                        .isSuccess(true)
                        .message("Yêu cầu rút tiền đã được chấp nhận và đang trong quá trình xử lý")
                        .accountHolderName(withdrawTicket.getBankAccount().getAccountHolderName())
                        .bankAccountNumber(withdrawTicket.getBankAccount().getBankAccountNumber())
                        .providerName(withdrawTicket.getBankAccount().getProviderName())
                        .build();

            } else {
                if (denyReason == null || denyReason.trim().isEmpty()) {
                    return MessageWithBankInformationResponse.builder()
                            .isSuccess(false)
                            .message("Bạn phải cung cấp lý do từ chối yêu cầu của khách hàng")
                            .build();
                }
                withdrawTicket.setDenyReason(denyReason.trim());
                withdrawTicket.setStatus(WithdrawStatus.REJECTED);
                withdrawTicketBusiness.update(withdrawTicket);

                log.debug("processWithdrawRequest() Rút tiền bị từ chối | withdrawTicketId: {}", withdrawTicketId);
                return MessageWithBankInformationResponse.builder()
                        .isSuccess(true)
                        .message("Yêu cầu rút tiền đã bị từ chối với lý do: " + denyReason.trim())
                        .build();
            }

        }catch (Exception e) {
            log.error("processWithdrawRequest() Unexpected Exception | message: {}", e.getMessage());
            return MessageWithBankInformationResponse.builder()
                    .isSuccess(false)
                    .message("Lỗi trong quá trình xử lý yêu cầu rút tiền")
                    .build();
        }
    }

@Override
    public MessageResponse uploadTransferBill(UUID withdrawTicketId, MultipartFile multipartFile) {
        log.debug("uploadTransferBill() Start | withdrawTicketId: {}, filename: {}", withdrawTicketId, multipartFile.getOriginalFilename());

        try {
            WithdrawTicket withdrawTicket = withdrawTicketBusiness.getById(withdrawTicketId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu rút tiền"));

            if (withdrawTicket.getStatus() != WithdrawStatus.APPROVED) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Chỉ có thể upload hóa đơn khi yêu cầu đang ở trạng thái APPROVED")
                        .build();
            }
            FileUploadUtil.AssertAllowedExtension(multipartFile, FileUploadUtil.IMAGE_PATTERN);
            String fileName = FileUploadUtil.formatFileName(multipartFile.getOriginalFilename());
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(multipartFile, fileName);
            withdrawTicket.setBankingBill(cloudinaryResponse.getResponseURL());
            withdrawTicket.setStatus(WithdrawStatus.COMPLETED);
            withdrawTicketBusiness.update(withdrawTicket);
            log.debug("uploadTransferBill() End | transferBillUrl: {}", cloudinaryResponse.getResponseURL());
            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Upload hóa đơn chuyển khoản thành công và cập nhật trạng thái COMPLETED")
                    .build();

        } catch (Exception e) {
            log.error("uploadTransferBill() Unexpected Exception | message: {}", e.getMessage());
            return MessageResponse.builder()
                    .isSuccess(false)
                    .message("Lỗi khi upload hóa đơn chuyển khoản: " + e.getMessage())
                    .build();
        }
    }


}
