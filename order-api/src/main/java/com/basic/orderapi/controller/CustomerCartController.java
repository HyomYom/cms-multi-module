package com.basic.orderapi.controller;

import com.basic.domain.config.JwtAuthenticationProvider;
import com.basic.orderapi.application.CartApplication;
import com.basic.orderapi.domain.product.AddProductCartForm;
import com.basic.orderapi.domain.redis.Cart;
import com.basic.orderapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    private final CartApplication cartApplication;
    private final JwtAuthenticationProvider provider;

    // 임시 코드


    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form) {
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));

    }

    @GetMapping
    public ResponseEntity<Cart> showChart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token){
        return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }
}
