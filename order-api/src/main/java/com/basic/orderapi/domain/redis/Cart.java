package com.basic.orderapi.domain.redis;


import com.basic.orderapi.domain.product.AddProductCartForm;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RedisHash("cart")
public class Cart {
    @Id
    private Long customerId;
    private List<Product> products = new ArrayList<>();
    private List<String> messages = new ArrayList<>();


    public Cart(Long customerId){
        this.customerId = customerId;
    }

    public void addMessage(String message){
        messages.add(message);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> items = new ArrayList<>();


        public static Cart.Product from(AddProductCartForm form) {
            return Cart.Product.builder()
                    .id(form.getId())
                    .sellerId((form.getSellerId()))
                    .name(form.getName())
                    .description(form.getDescription())
                    .items(form.getItems().stream().map(ProductItem::from).collect(Collectors.toList()))
                    .build();
        }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long id;
        private String name;
        private Integer count;
        private Integer price;


        public static ProductItem from(AddProductCartForm.ProductItem item) {
            return ProductItem.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .count(item.getCount())
                    .price(item.getPrice())
                    .build();
        }

    }
}
