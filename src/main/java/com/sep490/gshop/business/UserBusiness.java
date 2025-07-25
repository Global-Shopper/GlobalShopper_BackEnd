package com.sep490.gshop.business;

import com.sep490.gshop.entity.User;

import java.util.UUID;

public interface UserBusiness extends BaseBusiness<User> {
    User getUserByEmail(String email);
    User getByUserId(UUID userId);
}
