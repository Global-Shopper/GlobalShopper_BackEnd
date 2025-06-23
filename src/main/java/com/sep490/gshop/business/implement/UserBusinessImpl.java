package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.repository.UserRepository;
import org.springframework.stereotype.Component;

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
}
