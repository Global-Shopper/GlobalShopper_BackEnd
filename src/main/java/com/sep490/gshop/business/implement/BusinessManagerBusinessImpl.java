package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BusinessManagerBusiness;
import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.repository.ConfigurationRepository;
import com.sep490.gshop.repository.CustomerRepository;
import com.sep490.gshop.repository.specification.CustomSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BusinessManagerBusinessImpl implements BusinessManagerBusiness {

    private final ConfigurationRepository configurationRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BusinessManagerBusinessImpl(ConfigurationRepository configurationRepository, CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.configurationRepository = configurationRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Configuration updateServiceFee(Double serviceFee) {
        Configuration config = getConfig();
        if (config == null) {
            config = new Configuration();
        }
        config.setServiceFee(serviceFee);
        return configurationRepository.save(config);
    }

    @Override
    public Configuration getConfig() {
        return configurationRepository.findTopBy();
    }

    @Override
    public Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate) {
        Specification<Customer> spec = CustomSpecification.filterCustomer(
                search, status, startDate, endDate
        );
        return customerRepository.findAll(spec, pageable).map(customer -> modelMapper.map(customer, CustomerDTO.class));
    }
}
