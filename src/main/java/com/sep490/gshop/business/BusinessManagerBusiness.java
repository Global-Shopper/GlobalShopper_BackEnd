package com.sep490.gshop.business;

import com.sep490.gshop.entity.Configuration;

public interface BusinessManagerBusiness {
    Configuration updateServiceFee(Double serviceFee);

    Configuration getConfig();
}
