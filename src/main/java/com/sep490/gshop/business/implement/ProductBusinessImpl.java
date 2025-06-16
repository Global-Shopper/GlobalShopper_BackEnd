package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.ProductBusiness;
import com.sep490.gshop.entity.Product;
import com.sep490.gshop.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductBusinessImpl extends BaseBusinessImpl<Product, ProductRepository> implements ProductBusiness {
    protected ProductBusinessImpl(ProductRepository repository) {
        super(repository);
    }
}
