package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.repository.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerBusinessImpl extends BaseBusinessImpl<Customer, CustomerRepository> implements CustomerBusiness {

    public CustomerBusinessImpl(CustomerRepository repository) {
        super(repository);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }
}
