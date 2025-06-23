package com.sep490.gshop.business;

import com.sep490.gshop.entity.User;

public interface UserBusiness extends BaseBusiness<User> {
    User getUserByEmail(String email);
}
