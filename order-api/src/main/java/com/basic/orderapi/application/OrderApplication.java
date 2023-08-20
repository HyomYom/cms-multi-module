package com.basic.orderapi.application;


import com.basic.orderapi.client.UserClient;
import com.basic.orderapi.client.user.ChangeBalanceForm;
import com.basic.orderapi.client.user.CustomerDto;
import com.basic.orderapi.domain.model.ProductItem;
import com.basic.orderapi.domain.redis.Cart;
import com.basic.orderapi.exception.CustomException;
import com.basic.orderapi.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.IntStream;

import static com.basic.orderapi.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    @Transactional
    public void order(String token, Cart cart) {
        // 1번 : 주문 시 기존 카트 버림.
        // 2번 : 선택주문 : 내가 사지 않은 아이템을 살려야 함 -> 선택 주문 하지 않은 제품은 카트에 그대로 남아있어야 한다는 뜻[슉제]
        Cart orderCart = cartApplication.refreshCart(cart);
//        Cart orderCart = cartApplication.getCart(customerId);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }

        CustomerDto customerDto =
                userClient.getCustomerInfo(token).getBody();
        int totalPrice = getTotalPrice(cart);

        if (Objects.requireNonNull(customerDto).getBalance() < totalPrice) {
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        // 롤백 계획에 대해서 생각해야 함.

        userClient.changeBalance(token, ChangeBalanceForm.builder()
                .from("USER")
                .message("Order")
                .money(-totalPrice)
                .build()
        );

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());


            }
        }
        cartApplication.updateCart(customerDto.getId(), new Cart(customerDto.getId()));


    }

    public Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream().flatMapToInt(product ->
                        product.getItems().stream().flatMapToInt(productItem ->
                                IntStream.of(productItem.getPrice() * productItem.getCount())))
                .sum();
    }

    // 결제를 위해 필요한 것
    // 1번 : 물건들이 전부 주문 가능한 상태인지 확인
    // 2번 : 가격 변동이 있었는지에 대해 확인
    // 3번 : 고객의 돈이 충분한지.
    // 4번 : 결제 & 상품의 재고 관리

}
