package com.basic.orderapi.application;

import com.basic.orderapi.domain.model.Product;
import com.basic.orderapi.domain.product.AddProductCartForm;
import com.basic.orderapi.domain.product.AddProductForm;
import com.basic.orderapi.domain.product.AddProductItemForm;
import com.basic.orderapi.domain.redis.Cart;
import com.basic.orderapi.domain.repository.ProductRepository;
import com.basic.orderapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class CartApplicationTest {
    @Autowired
    private CartApplication cartApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_AND_REFRESH_TEST() {
        Product p = add_product();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        // 나머지 필드들에 대한 검즘

        assertNotNull(result);
        assertEquals(result.getName(), "나이키 에어포스");

        Long customerId = 100L;
        Cart cart = cartApplication.addCart(customerId, makeForm(result));
        assertEquals(cart.getCustomerId(), 100L);
        assertEquals(cart.getMessages().size(), 0);


        // 데이터가 잘 들어갔는지
        Cart cart1 = cartApplication.getCart(customerId);
        assertEquals(cart1.getProducts().get(0).getName(),"나이키 에어포스");


    }

    AddProductCartForm makeForm(Product p) {
        List<AddProductCartForm.ProductItem> productItems = new ArrayList<>();
        AddProductCartForm.ProductItem productItem =
                AddProductCartForm.ProductItem.builder()
                        .id(p.getProductItems().get(0).getId())
                        .price(p.getProductItems().get(0).getPrice())
                        .count(p.getProductItems().get(0).getCount())
                        .name(p.getProductItems().get(0).getName())
                        .build();
        productItems.add(productItem);
        AddProductCartForm form =
                AddProductCartForm.builder()
                        .sellerId(p.getSellerId())
                        .name(p.getName())
                        .items(productItems)
                        .description(p.getDescription())
                        .id(p.getId())
                        .build();
        return form;
    }


    Product add_product() {
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스", "신발", 10);
        Product p = productService.addProduct(sellerId, form);
        return p;
    }


    private static AddProductForm makeProductForm(String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();

    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();
    }

}