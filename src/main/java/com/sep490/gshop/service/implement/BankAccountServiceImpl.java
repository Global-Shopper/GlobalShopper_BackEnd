package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.BankAccountBusiness;
import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.*;
import com.sep490.gshop.payload.dto.BankAccountDTO;
import com.sep490.gshop.payload.request.BankAccountRequest;
import com.sep490.gshop.payload.request.BankAccountUpdateRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.BankAccountService;
import com.sep490.gshop.utils.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountBusiness bankAccountBusiness;
    private CustomerBusiness customerBusiness;
    private WalletBusiness walletBusiness;
    private final ModelMapper modelMapper;
    @Autowired
    public BankAccountServiceImpl(ModelMapper modelMapper, BankAccountBusiness bankAccountBusiness, WalletBusiness walletBusiness, CustomerBusiness customerBusiness) {
        this.modelMapper = modelMapper;
        this.bankAccountBusiness = bankAccountBusiness;
        this.walletBusiness = walletBusiness;
        this.customerBusiness = customerBusiness;
    }


    @Override
    public BankAccountDTO createBankAccount(BankAccountRequest bankAccountRequest) {
        log.debug("createBankAccount() Start | request: {}", bankAccountRequest);
        try {
            BankAccount bankAccount = modelMapper.map(bankAccountRequest, BankAccount.class);
            UUID currentUserId = AuthUtils.getCurrentUserId();
            var currentCustomer = customerBusiness.getById(currentUserId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng"));
            bankAccount.setCustomer(currentCustomer);
            Wallet wallet = walletBusiness.getById(currentCustomer.getWallet().getId())
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy ví, vui lòng tạo mới"));
            bankAccount.setWallet(wallet);
            if (bankAccountRequest.isDefault()) {
                unsetDefaultForUser(currentUserId);
            }
            if(bankAccountBusiness.existsBankAccount(bankAccountRequest.getBankAccountNumber(), currentUserId)){
                throw AppException.builder().code(400).message("Số tài khoản ngân hàng đã tồn tại").build();
            }
            BankAccount saved = bankAccountBusiness.create(bankAccount);
            BankAccountDTO dto = modelMapper.map(saved, BankAccountDTO.class);
            log.debug("createBankAccount() End | result: {}", dto);
            return dto;
        }catch (Exception e) {
            log.error("createBankAccount() Unexpected Exception | request: {}, message: {}", bankAccountRequest, e.getMessage());
            throw e;
        }
    }


    private void unsetDefaultForUser(UUID userId) {
        var list = bankAccountBusiness.findBankAccountsByCustomer(userId).stream()
                .map(sa -> modelMapper.map(sa, BankAccount.class))
                .toList();
        for(BankAccount defaultCheck : list) {
            if(defaultCheck.isDefault()){
                defaultCheck.setDefault(false);
            }
        }
    }

    @Override
    public BankAccountDTO updateBankAccount(UUID id, BankAccountUpdateRequest bankAccountRequest) {
        log.debug("updateBankAccount() Start | request: {}", bankAccountRequest);
        try {
            BankAccount existing = bankAccountBusiness.getById(id)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy tài khoản ngân hàng với ID: " + id));
            Customer currentCustomer = customerBusiness.getById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new AppException(401, "Bạn cần đăng nhập để sử dụng dịch vụ"));
            if(existing.getCustomer().getId() != currentCustomer.getId()){
                throw AppException.builder().code(400).message("Bạn không có quyền xem hoặc chỉnh sửa tài khoản ngân hàng này").build();
            }
            modelMapper.map(bankAccountRequest, existing);
            if (bankAccountRequest.getIsDefault()) {
                unsetDefaultForUser(currentCustomer.getId());
            }
            existing.setDefault(bankAccountRequest.getIsDefault());
            BankAccount updated = bankAccountBusiness.update(existing);
            BankAccountDTO dto = modelMapper.map(updated, BankAccountDTO.class);
            log.debug("updateBankAccount() End | result: {}", dto);
            return dto;
        }catch (Exception e) {
            log.error("updateBankAccount() Unexpected Exception | request: {}, message: {}", bankAccountRequest, e.getMessage());
            throw e;
        }
    }


    @Override
    public BankAccountDTO getBankAccountByCurrent(UUID bankAccountId) {
        try {
            Customer currentCustomer = customerBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> new AppException(401, "Bạn cần đăng nhập để sử dụng dịch vụ"));
            BankAccount bankAccount = bankAccountBusiness.getById(bankAccountId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy tài khoản ngân hàng")
                            .code(404)
                            .build());
            if (!bankAccount.getCustomer().getId().equals(currentCustomer.getId())) {
                throw AppException.builder()
                        .message("Bạn không có quyền xem hoặc chỉnh sửa tài khoản ngân hàng này")
                        .code(400)
                        .build();
            }
            BankAccountDTO dto = modelMapper.map(bankAccount, BankAccountDTO.class);
            log.debug("getBankAccountByCurrent() End | result: {}", dto);
            return dto;
        }catch (Exception e) {
            log.error("getBankAccountByCurrent() Unexpected Exception | bankAccountId: {}, message: {}", bankAccountId, e.getMessage());
            throw e;
        }
    }


    @Override
    public MessageResponse deleteBankAccount(UUID bankAccountId) {
        try {
            Customer currentCustomer = customerBusiness.getById(AuthUtils.getCurrentUserId())
                    .orElseThrow(() -> new AppException(401, "Bạn cần đăng nhập để sử dụng dịch vụ"));
            BankAccount bankAccount = bankAccountBusiness.getById(bankAccountId)
                    .orElseThrow(() -> AppException.builder()
                            .message("Không tìm thấy tài khoản ngân hàng")
                            .code(404)
                            .build());
            if (!bankAccount.getCustomer().getId().equals(currentCustomer.getId())) {
                throw AppException.builder()
                        .message("Bạn không có quyền xem hoặc chỉnh sửa tài khoản ngân hàng này")
                        .code(400)
                        .build();
            }
            boolean check = bankAccountBusiness.delete(bankAccountId);
            log.debug("deleteBankAccount() End | bankAccountId: {}, success: {}", bankAccountId, check);
            return MessageResponse.builder()
                    .message("Xoá thành công tài khoản ngân hàng")
                    .isSuccess(check)
                    .build();
        }catch (Exception e) {
            log.error("deleteBankAccount() Unexpected Exception | bankAccountId: {}, message: {}", bankAccountId, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<BankAccountDTO> getAllBankAccountsByCurrent() {
        try {
            User user = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            List<BankAccountDTO> list = bankAccountBusiness.findBankAccountsByCustomer(user.getId()).stream()
                    .map(x -> modelMapper.map(x, BankAccountDTO.class))
                    .toList();
            log.debug("getAllBankAccountsByCurrent() End | Size: {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("getAllBankAccountsByCurrent() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }

}
