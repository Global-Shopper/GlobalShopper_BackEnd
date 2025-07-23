package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.*;
import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TransactionBusiness transactionBusiness;
    @Autowired
    public WalletServiceImpl(WalletBusiness walletBusiness,
                             CustomerBusiness customerBusiness,
                             ModelMapper modelMapper,
                             VNPayServiceImpl vnPayServiceImpl,
                             BankAccountBusiness bankAccountBusiness,
                             UserBusiness userBusiness,
                             WithdrawTicketBusiness withdrawTicketBusiness,
                             CloudinaryService cloudinaryService,
                             TransactionBusiness transactionBusiness) {
        this.walletBusiness = walletBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
        this.vnPayServiceImpl = vnPayServiceImpl;
        this.bankAccountBusiness = bankAccountBusiness;
        this.userBusiness = userBusiness;
        this.withdrawTicketBusiness = withdrawTicketBusiness;
        this.cloudinaryService = cloudinaryService;
        this.transactionBusiness = transactionBusiness;
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


            String txnRef = UUID.randomUUID().toString().replace("-", "");
            Transaction transaction = Transaction.builder()
                    .amount(request.getBalance())
                    .type(TransactionType.DEPOSIT)
                    .customer(customer)
                    .balanceBefore(wallet.getBalance())
                    .balanceAfter(wallet.getBalance() + request.getBalance())
                    .status(TransactionStatus.PENDING)
                    .description("Nạp tiền vào tài khoản")
                    .referenceCode(txnRef)
                    .build();
            transactionBusiness.create(transaction);
            var url = vnPayServiceImpl.createURL(request.getBalance(),
                    "Nạp tiền vào tài khoản " + customer.getName(),
                    customer.getEmail(), txnRef, request.getRedirectUri());

            log.debug("depositMoney() End | url: {}", url);
            String formattedAmount = formatAmount(request.getBalance());
            return MoneyChargeResponse.builder()
                    .isSuccess(true)
                    .message("Đã yêu cầu nạp thành công số tiền " + formattedAmount + " VNĐ vào ví của bạn")
                    .url(url)
                    .build();

        } catch (Exception e) {
            log.error("depositMoney() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public boolean processVNPayReturn(String email, String status, String amount, String vnpTxnRef) {
        log.debug("processVNPayReturn() Start | email: {}", email);

        Transaction transaction = new Transaction();
        try {
            if (amount == null || amount.length() < 3) {
                log.error("Amount không hợp lệ: {}", amount);
                return false;
            }
            String trimmedAmountStr = amount.substring(0, amount.length() - 2);
            double amountGet = Double.parseDouble(trimmedAmountStr);

            transaction.setAmount(amountGet);
            transaction.setType(TransactionType.DEPOSIT);

            if (email == null || email.isEmpty()) {
                log.error("Email không được để trống");
                return false;
            }
            Customer customer = customerBusiness.findByEmail(email);
            if (customer == null) {
                transaction.setStatus(TransactionStatus.FAIL);
                transaction.setDescription("Không tìm thấy người dùng, thử lại sau");
                transaction.setCustomer(null);
                transactionBusiness.create(transaction);
                log.error("Không tìm thấy khách hàng với email: {}", email);
                return false;
            }
            transaction.setCustomer(customer);

            Wallet wallet = walletBusiness.getById(customer.getWallet().getId())
                    .orElseThrow(() -> {
                        transaction.setStatus(TransactionStatus.FAIL);
                        transaction.setDescription("Không tìm thấy ví của bạn");
                        transactionBusiness.create(transaction);
                        log.error("Không tìm thấy ví cho khách hàng id: {}", customer.getId());
                        return new AppException(404, "Không tìm thấy ví của bạn");
                    });

            double balanceBefore = wallet.getBalance();
            transaction.setBalanceBefore(balanceBefore);

            if (!"00".equals(status)) {
                transaction.setStatus(TransactionStatus.FAIL);
                transaction.setDescription("Nạp tiền vào tài khoản không thành công");
                transaction.setBalanceAfter(balanceBefore);
                transactionBusiness.create(transaction);
                log.warn("Thanh toán thất bại với mã trạng thái: {}", status);
                return false;
            }

            wallet.setBalance(balanceBefore + amountGet);
            walletBusiness.update(wallet);

            transaction.setBalanceAfter(wallet.getBalance());
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setDescription("Nạp tiền vào tài khoản thành công");
            transactionBusiness.create(transaction);

            log.debug("processVNPayReturn() End | updated balance: {}", wallet.getBalance());

            return true;

        } catch (Exception e) {
            log.error("processVNPayReturn() Unexpected Exception | message: {}", e.getMessage());
            return false;
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
            withdrawTicket.setWallet(wallet);
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

        Transaction transaction = new Transaction();
        try {
            WithdrawTicket withdrawTicket = withdrawTicketBusiness.getById(withdrawTicketId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy yêu cầu rút tiền"));

            if (withdrawTicket.getStatus() != WithdrawStatus.APPROVED) {
                return MessageResponse.builder()
                        .isSuccess(false)
                        .message("Chỉ có thể upload hóa đơn khi yêu cầu đang ở trạng thái APPROVED")
                        .build();
            }

            Customer customer = customerBusiness.findByWallet(withdrawTicket.getWallet().getId());

            FileUploadUtil.AssertAllowedExtension(multipartFile, FileUploadUtil.IMAGE_PATTERN);

            String fileName = FileUploadUtil.formatFileName(multipartFile.getOriginalFilename());
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(multipartFile, fileName);

            withdrawTicket.setBankingBill(cloudinaryResponse.getResponseURL());
            withdrawTicket.setStatus(WithdrawStatus.COMPLETED);
            withdrawTicketBusiness.update(withdrawTicket);

            Wallet wallet = walletBusiness.getById(withdrawTicket.getWallet().getId())
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy ví của bạn, vui lòng thử lại")
                            .code(404)
                            .build());

            double balanceBefore = wallet.getBalance();

            if (balanceBefore < withdrawTicket.getAmount()) {
                throw AppException.builder()
                        .code(400)
                        .message("Số tiền rút lớn hơn số tiền trong ví, vui lòng thử lại")
                        .build();
            }

            wallet.setBalance(balanceBefore - withdrawTicket.getAmount());
            walletBusiness.update(wallet);

            transaction.setCustomer(customer);
            transaction.setBalanceBefore(balanceBefore);
            transaction.setAmount(withdrawTicket.getAmount());
            transaction.setBalanceAfter(wallet.getBalance());
            transaction.setDescription("Rút tiền về tài khoản");
            transaction.setType(TransactionType.WITHDRAW);
            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionBusiness.create(transaction);

            log.debug("uploadTransferBill() End | transferBillUrl: {}", cloudinaryResponse.getResponseURL());

            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Upload hóa đơn chuyển khoản thành công và cập nhật trạng thái COMPLETED")
                    .build();

        } catch (Exception e) {
            log.error("uploadTransferBill() Unexpected Exception | message: {}", e.getMessage());

            try {
                transaction.setStatus(TransactionStatus.FAIL);
                transaction.setDescription("Lỗi khi upload hóa đơn chuyển khoản: " + e.getMessage());
                transactionBusiness.create(transaction);
            } catch (Exception ex) {
                log.error("Không thể lưu transaction thất bại: {}", ex.getMessage(), ex);
            }

            return MessageResponse.builder()
                    .isSuccess(false)
                    .message("Lỗi khi upload hóa đơn chuyển khoản: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public void ipnCallback(HttpServletRequest request) {
        try {
            log.debug("ipnCallback() WalletServiceImpl Callback Start");
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values.length > 0) {
                    params.put(key, values[0]);
                }
            });
            String status = vnPayServiceImpl.handleVNPayIPN(params);
            String vnpTxnRef = params.get("vnp_TxnRef");

            Transaction transaction = transactionBusiness.getTransactionByReferenceCode(vnpTxnRef);
            if (transaction == null) {
                log.error("IPN Callback: Transaction not found for ref: {}", vnpTxnRef);
                return;
            }
            if ("00".equals(status)) {
                transaction.setStatus(TransactionStatus.SUCCESS);
                Wallet wallet = walletBusiness.getById(transaction.getCustomer().getWallet().getId())
                        .orElseThrow(() -> {
                            transaction.setStatus(TransactionStatus.FAIL);
                            transaction.setDescription("Không tìm thấy ví của bạn");
                            transactionBusiness.create(transaction);
                            throw new AppException(404, "Không tìm thấy ví của bạn");
                        });
                double balanceBefore = wallet.getBalance();
                transaction.setBalanceBefore(balanceBefore);
                wallet.setBalance(balanceBefore + transaction.getAmount());
                walletBusiness.update(wallet);
                log.debug("ipnCallback() WalletServiceImpl | Successfully processed transaction with ref: {}", vnpTxnRef);
            } else {
                transaction.setStatus(TransactionStatus.FAIL);
                log.debug("ipnCallback() WalletServiceImpl | Failed to process transaction with ref: {}", vnpTxnRef);
            }
            log.info("IPN Callback: ReferenceCode: {} | status: {} | transactionStatus: {}", transaction.getReferenceCode(), transaction.getStatus(), params.get("vnp_TransactionStatus"));
            transactionBusiness.update(transaction);
        } catch (Exception e) {
            log.error("IPN Callback Exception | message: {}", e.getMessage());
        }
    }


}
