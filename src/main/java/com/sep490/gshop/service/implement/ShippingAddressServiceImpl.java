package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.ShippingAddress;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.ShippingAddressRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.service.ShippingAddressService;
import com.sep490.gshop.utils.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class ShippingAddressServiceImpl implements ShippingAddressService {
    private final ShippingAddressBusiness shippingAddressBusiness;
    private final ModelMapper modelMapper;
    @Autowired
    public ShippingAddressServiceImpl(ShippingAddressBusiness shippingAddressBusiness, ModelMapper modelMapper) {
        this.shippingAddressBusiness = shippingAddressBusiness;
        this.modelMapper = modelMapper;
    }
    @Override
    @Transactional
    public ShippingAddressDTO createShippingAddress(ShippingAddressRequest request) {
        log.debug("createShippingAddress() Start | request: {}", request);
        try {
            User currentUser = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            if (currentUser == null) {
                throw new AppException(401, "Vui lòng đăng nhập để sử dụng dịch vụ");
            }

            ShippingAddress newAddress = modelMapper.map(request, ShippingAddress.class);
            newAddress.setCustomer(modelMapper.map(currentUser, Customer.class));

            if (newAddress.isDefault()) {
                setDefaultShippingAddress();
                newAddress.setDefault(true);
            }

            ShippingAddress created = shippingAddressBusiness.create(newAddress);
            ShippingAddressDTO dto = modelMapper.map(created, ShippingAddressDTO.class);

            log.debug("createShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createShippingAddress() Exception | request: {}, message: {}", request, e.getMessage());
            throw e;
        }
    }
    @Override
    @Transactional
    public ShippingAddressDTO updateDefaultShippingAddress(ShippingAddressRequest request, UUID shippingAddressId) {
        log.debug("updateShippingAddress() Start | id: {}, request: {}", shippingAddressId, request);
        try {
            User currentUser = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            ShippingAddress address = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Địa chỉ không tồn tại"));

            if (!address.getCustomer().getId().equals(currentUser.getId())) {
                throw new AppException(403, "Bạn không có quyền sửa địa chỉ này");
            }
            modelMapper.map(request, address);
            if (address.isDefault()) {
                setDefaultShippingAddress();
                address.setDefault(true);
            }
            ShippingAddress updated = shippingAddressBusiness.update(address);
            ShippingAddressDTO dto = modelMapper.map(updated, ShippingAddressDTO.class);
            log.debug("updateShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("updateShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage());
            throw e;
        }
    }

    @Override
    public ShippingAddressDTO getShippingAddress(UUID shippingAddressId) {
        log.debug("getShippingAddress() Start | id: {}", shippingAddressId);
        try {
            User currentUser = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            ShippingAddress address = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Địa chỉ không tồn tại"));
            if (!address.getCustomer().getId().equals(currentUser.getId())) {
                throw new AppException(403, "Bạn không có quyền xem địa chỉ này");
            }
            ShippingAddressDTO dto = modelMapper.map(address, ShippingAddressDTO.class);
            log.debug("getShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("getShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ShippingAddressDTO> getShippingAddressesByCurrentUser() {
        log.debug("getShippingAddresses() Start");
        try {
            User user = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            List<ShippingAddressDTO> list = shippingAddressBusiness.findShippingAddressByUserId(user.getId()).stream()
                    .map(x -> modelMapper.map(x, ShippingAddressDTO.class))
                    .toList();
            log.debug("getShippingAddresses() End | Size: {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("getShippingAddresses() Exception | message: {}", e.getMessage());
            throw e;
        }
    }
    @Override
    public List<ShippingAddressDTO> getShippingAddresses() {
        log.debug("getShippingAddresses() Start - Admin only");
        try {
            List<ShippingAddress> addresses = shippingAddressBusiness.getAll();
            List<ShippingAddressDTO> dtos = addresses.stream()
                    .map(sa -> modelMapper.map(sa, ShippingAddressDTO.class))
                    .toList();
            log.debug("getShippingAddresses() End | size: {}", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("getShippingAddresses() Exception | message: {}", e.getMessage());
            throw e;
        }
    }
    @Override
    @Transactional
    public boolean deleteShippingAddress(UUID shippingAddressId) {
        log.debug("deleteShippingAddress() Start | id: {}", shippingAddressId);
        try {
            User user = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
            ShippingAddress address = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Địa chỉ không tồn tại"));
            if (!address.getCustomer().getId().equals(user.getId())) {
                throw new AppException(403, "Bạn không có quyền xóa địa chỉ này");
            }
            boolean result = shippingAddressBusiness.delete(shippingAddressId);
            log.debug("deleteShippingAddress() End | id: {}, result: {}", shippingAddressId, result);
            return result;
        } catch (Exception e) {
            log.error("deleteShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateDefaultShippingAddress(UUID shippingAddressId) {
        try {
            UUID id = AuthUtils.getCurrentUser().getId();
            log.debug("updateShippingAddress() Start | id: {}", id);
            if (shippingAddressId == null) {
                throw AppException.builder().code(401).message("Vui lòng đăng nhập để sử dụng dịch vụ").build();
            }
            ShippingAddress address = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Địa chỉ không tồn tại"));
            if (!address.getCustomer().getId().equals(id)) {
                throw AppException.builder().message("Bạn không có quyền sửa địa chỉ này").build();
            }
            address.setDefault(true);
            shippingAddressBusiness.update(address);
            log.debug("updateShippingAddress() End | id: {}", id);
            return address.isDefault();
        }catch (Exception e) {
            log.error("updateShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage());
            throw e;
        }
    }

    public boolean IsDefaultShippingAddress() {
        User user = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
        var list = shippingAddressBusiness.findShippingAddressByUserId(user.getId()).stream()
                .map(sa -> modelMapper.map(sa, ShippingAddress.class))
                .toList();
        var addedList = new ArrayList<ShippingAddress>();
        for(ShippingAddress defaultCheck : list) {
            if(defaultCheck.isDefault()){
                addedList.add(defaultCheck);
            }
        }
        return !addedList.isEmpty();
    }
    public void setDefaultShippingAddress() {
        User user = modelMapper.map(AuthUtils.getCurrentUser(), User.class);
        var list = shippingAddressBusiness.findShippingAddressByUserId(user.getId()).stream()
                .map(sa -> modelMapper.map(sa, ShippingAddress.class))
                .toList();
        for(ShippingAddress defaultCheck : list) {
            if(defaultCheck.isDefault()){
                    defaultCheck.setDefault(false);
            }
        }

    }
}
