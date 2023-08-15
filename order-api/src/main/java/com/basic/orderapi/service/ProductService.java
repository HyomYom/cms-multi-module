package com.basic.orderapi.service;

import com.basic.orderapi.domain.model.Product;
import com.basic.orderapi.domain.product.AddProductForm;
import com.basic.orderapi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;


    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form){
        return productRepository.save(Product.of(sellerId, form));
    }
}
