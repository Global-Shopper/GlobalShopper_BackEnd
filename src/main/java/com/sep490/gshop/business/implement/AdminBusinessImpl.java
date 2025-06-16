package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.AdminBusiness;
import com.sep490.gshop.entity.Admin;
import com.sep490.gshop.repository.AdminRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminBusinessImpl extends BaseBusinessImpl<Admin, AdminRepository> implements AdminBusiness {
    protected AdminBusinessImpl(AdminRepository repository) {
        super(repository);
    }
}
