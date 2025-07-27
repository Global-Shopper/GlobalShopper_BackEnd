package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserBusinessImpl extends BaseBusinessImpl<User, UserRepository> implements UserBusiness {
    protected UserBusinessImpl(UserRepository repository) {
        super(repository);
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return repository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public User getByUserId(UUID userId) {
        User user = repository.findById(userId).orElse(null);
        if (user instanceof Customer customer){
            return customer;
        } else if (user instanceof Admin admin) {
            return admin;
        } else {
            return user;
        }
    }
}
