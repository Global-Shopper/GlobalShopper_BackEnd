package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BusinessManagerBusiness;
import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessManagerBusinessImpl implements BusinessManagerBusiness {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public BusinessManagerBusinessImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
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
}
