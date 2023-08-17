package com.basic.orderapi.service;

import com.basic.orderapi.domain.model.Product;
import com.basic.orderapi.domain.product.AddProductForm;
import com.basic.orderapi.domain.product.AddProductItemForm;
import com.basic.orderapi.domain.product.UpdateProductForm;
import com.basic.orderapi.domain.product.UpdateProductItemForm;
import com.basic.orderapi.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSearchService productSearchService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_PRODUCT_TEST() {
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스", "멋집니다.", 3);

        Product p = productService.addProduct(sellerId, form);

        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);
        assertEquals(result.getName(), "나이키 에어포스");
        assertEquals(result.getDescription(), "멋집니다.");
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "나이키 에어포스0");
    }

    @Test
    void UPDATE_PRODUCT_TEST() {
        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스", "멋집니다.", 3);
        Product p = productService.addProduct(sellerId, form);

        UpdateProductForm uForm = makeUpdateProductForm(p.getSellerId(), "나이키 덩크 조던", "훨씬 멋짐.", 3, 0L);


        Product up = productService.updateProduct(p.getSellerId(), uForm);


        Product result = productRepository.findWithProductItemsById(up.getId()).get();

        assertEquals(result.getName(), "나이키 덩크 조던");
        assertEquals(result.getProductItems().get(0).getName(), "나이키 덩크 조던(신상)");

    }

    @Test
    void DELETE_PRODUCT_TEST() {

        Long sellerId = 1L;
        AddProductForm form = makeProductForm("나이키 에어포스", "멋집니다.", 3);
        Product p = productService.addProduct(sellerId, form);
        Optional<Product> result = productRepository.findWithProductItemsById(p.getId());
        productService.deleteProduct(sellerId, result.get().getId());

        assertEquals(productRepository.findWithProductItemsById(p.getId()), Optional.empty());


    }

    @Test
    void SEARCH_PRODUCT_TEST() {
        Long sellerId = 1L;
        AddProductForm form1 = makeProductForm("나이키 에어포스", "멋집니다.", 3);
        AddProductForm form2 = makeProductForm("조던 에어포스", "멋집니다.", 3);
        Product p1 = productService.addProduct(sellerId, form1);
        Product p2 = productService.addProduct(sellerId, form2);
        List<Long> list = new ArrayList<>();
        list.add(p1.getId());
        list.add(p2.getId());
        List<Product> airforce = productSearchService.searchByName("에어포스");
        List<Product> listByProductIds = productSearchService.getListByProductIds(list);

        assertEquals(airforce.size(), 2);
        assertEquals(airforce.get(0).getName(), "나이키 에어포스");
        assertEquals(airforce.get(1).getName(), "조던 에어포스");

        assertEquals(listByProductIds.size(), 2);
        assertEquals(listByProductIds.get(0).getName(), "나이키 에어포스");


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

    private static UpdateProductForm makeUpdateProductForm(Long id, String name, String description, int itemCount, Long itemId) {
        List<UpdateProductItemForm> itemForms = new ArrayList<>();

        if (itemId != null) {
            itemForms.add(makeUpdateProductItemForm(id, itemId, name + "(신상)", 1000, 10));
        }

        return UpdateProductForm.builder()
                .id(id)
                .name(name)
                .description(description)
                .items(itemForms)
                .build();

    }

    private static UpdateProductItemForm makeUpdateProductItemForm(Long id, Long productId, String name, Integer price, Integer count) {
        return UpdateProductItemForm.builder()
                .id(id)
                .productId(productId)
                .name(name)
                .price(price)
                .count(count)
                .build();
    }

}