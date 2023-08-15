package com.basic.orderapi.service;

import com.basic.orderapi.domain.model.Product;
import com.basic.orderapi.domain.model.ProductItem;
import com.basic.orderapi.domain.product.AddProductItemForm;
import com.basic.orderapi.domain.repository.ProductItemRepository;
import com.basic.orderapi.domain.repository.ProductRepository;
import com.basic.orderapi.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.basic.orderapi.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.basic.orderapi.exception.ErrorCode.SAME_ITEM_NAME;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;


    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form){
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        if(product.getProductItems().stream()
                .anyMatch(item -> item.getName().equals(form.getName()))){
            throw  new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        return product;
    }


}
