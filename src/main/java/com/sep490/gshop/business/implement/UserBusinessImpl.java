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
}
