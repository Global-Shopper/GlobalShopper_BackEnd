package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.payload.dto.WalletDTO;
import com.sep490.gshop.payload.request.WalletRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.MoneyChargeResponse;
import com.sep490.gshop.service.WalletService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {

    private final WalletBusiness walletBusiness;
    private final CustomerBusiness customerBusiness;
    private final ModelMapper modelMapper;
    private VNPayServiceImpl vnPayServiceImpl;
    @Autowired
    public WalletServiceImpl(WalletBusiness walletBusiness, CustomerBusiness customerBusiness, ModelMapper modelMapper, VNPayServiceImpl vnPayServiceImpl) {
        this.walletBusiness = walletBusiness;
        this.customerBusiness = customerBusiness;
        this.modelMapper = modelMapper;
        this.vnPayServiceImpl = vnPayServiceImpl;
    }

    @Override
    public MoneyChargeResponse depositMoney(WalletRequest request) {
       Customer customer = customerBusiness.getById(AuthUtils.getCurrentUserId()).orElseThrow(() -> AppException.builder().message("Bạn cần đăng nhập để sử dụng dịch vụ").code(401).build());
       Wallet wallet = walletBusiness.getById(customer.getWallet().getId()).orElseThrow(() -> AppException.builder().code(404).message("Không tìm thấy ví của bạn").build());
        var url = vnPayServiceImpl.createURL(request.getBalance(), "Nạp tiền vào tài khoản " + customer.getName(), customer.getEmail());
        return MoneyChargeResponse.builder().isSuccess(true).message("Đã yêu cầu nạp thành công số tiền "+ request.getBalance() + " vào ví của bạn").url(url).build();
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

            return MessageResponse.builder()
                    .isSuccess(true)
                    .message("Nạp tiền thành công: " + amount + " VND vào ví")
                    .build();
        }catch (Exception e) {
            log.error("processVNPayReturn() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public MoneyChargeResponse withdrawMoneyRequest(WalletRequest request) {
        return null;
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

        } catch (AppException ae) {
            log.error("getWalletByCurrent() AppException | message: {}", ae.getMessage());
            throw ae;
        } catch (Exception e) {
            log.error("getWalletByCurrent() Unexpected Exception | message: {}", e.getMessage(), e);
            throw e;
        }
    }


}
