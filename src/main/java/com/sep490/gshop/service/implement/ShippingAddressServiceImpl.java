package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.ShippingAddressBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.ShippingAddress;
import com.sep490.gshop.payload.dto.RefundTicketDTO;
import com.sep490.gshop.payload.dto.ShippingAddressDTO;
import com.sep490.gshop.payload.request.ShippingAddressRequest;
import com.sep490.gshop.service.ShippingAddressService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ShippingAddressDTO createShippingAddress(ShippingAddressRequest shippingAddressRequest) {
        log.debug("createShippingAddress() Start | request: {}", shippingAddressRequest);
        try {
            ShippingAddress newAddress = modelMapper.map(shippingAddressRequest, ShippingAddress.class);
            newAddress.setId(UUID.randomUUID());
            newAddress.setCustomer(null); // Cần xem lại logic này nếu có liên kết với Customer
            ShippingAddress createdAddress = shippingAddressBusiness.create(newAddress);
            ShippingAddressDTO dto = modelMapper.map(createdAddress, ShippingAddressDTO.class);
            log.debug("createShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("createShippingAddress() Exception | request: {}, message: {}", shippingAddressRequest, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ShippingAddressDTO updateShippingAddress(ShippingAddressRequest shippingAddressRequest, UUID shippingAddressId) {
        log.debug("updateShippingAddress() Start | id: {}, request: {}", shippingAddressId, shippingAddressRequest);
        try {
            ShippingAddress addressFound = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Address Not Found"));
            modelMapper.map(shippingAddressRequest, addressFound);
            ShippingAddress updatedAddress = shippingAddressBusiness.update(addressFound);
            ShippingAddressDTO dto = modelMapper.map(updatedAddress, ShippingAddressDTO.class);
            log.debug("updateShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("updateShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ShippingAddressDTO getShippingAddress(UUID shippingAddressId) {
        log.debug("getShippingAddress() Start | id: {}", shippingAddressId);
        try {
            ShippingAddress address = shippingAddressBusiness.getById(shippingAddressId)
                    .orElseThrow(() -> new AppException(404, "Address Not Found"));
            ShippingAddressDTO dto = modelMapper.map(address, ShippingAddressDTO.class);
            log.debug("getShippingAddress() End | dto: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("getShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ShippingAddressDTO> getShippingAddresses() {
        log.debug("getShippingAddresses() Start");
        try {
            List<ShippingAddressDTO> list = shippingAddressBusiness.getAll().stream()
                    .map(sa -> modelMapper.map(sa, ShippingAddressDTO.class))
                    .toList();
            log.debug("getShippingAddresses() End | Size: {}", list.size());
            return list;
        } catch (Exception e) {
            log.error("getShippingAddresses() Exception | message: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteShippingAddress(UUID shippingAddressId) {
        log.debug("deleteShippingAddress() Start | id: {}", shippingAddressId);
        try {
            boolean result = shippingAddressBusiness.delete(shippingAddressId);
            log.debug("deleteShippingAddress() End | id: {}, result: {}", shippingAddressId, result);
            return result;
        } catch (Exception e) {
            log.error("deleteShippingAddress() Exception | id: {}, message: {}", shippingAddressId, e.getMessage(), e);
            throw e;
        }
    }
}
