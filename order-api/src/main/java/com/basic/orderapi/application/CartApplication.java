package com.basic.orderapi.application;

import com.basic.orderapi.domain.model.Product;
import com.basic.orderapi.domain.model.ProductItem;
import com.basic.orderapi.domain.product.AddProductCartForm;
import com.basic.orderapi.domain.redis.Cart;
import com.basic.orderapi.exception.CustomException;
import com.basic.orderapi.service.CartService;
import com.basic.orderapi.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.basic.orderapi.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.basic.orderapi.exception.ErrorCode.NOT_FOUND_PRODUCT;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {
        Product product = productSearchService.getByProductId(form.getId());
        log.info(product.getName());

        if(ObjectUtils.isEmpty(product)){
            throw new CustomException(NOT_FOUND_PRODUCT);
        }

        log.info(product.getName());
        Cart cart = cartService.getCart(customerId);
        if(!cart.getProducts().isEmpty() && !addAble(cart, product, form)) {

            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);

        }
        return cartService.addCart(customerId, form);

    }
    public Cart updateCart(Long customerId, Cart cart){
        cartService.putCart(customerId,cart);
        return getCart(customerId);
    }


    //1. 장바구니에 상품을 추가 했다.
    //2. 상품의 가격이나 수량이 변동 된다.
    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(), cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(cart.getCustomerId());
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        // 메세지 없는 것
        cartService.putCart(customerId,cart);
        return  returnCart;

        // 2. 메세지를 보고 난 다음에는, 이미 본 메세지는 스팸이 되기 떄문에 제거한다.
    }


    protected Cart refreshCart(Cart cart) {
        // 1. 상품이나 상품의 아이템의 정보, 가격 수량이 변경되었는지 체크하고
        // 그에 맞는 알람을 제공한다.
        // 2. 상품의 수량, 가격을 우리가 임의로 변경한다.

        Map<Long, Product> productMap = productSearchService.getListByProductIds(cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0 ; i < cart.getProducts().size(); i++) {
            Cart.Product cartProduct = cart.getProducts().get(i);
            Product p = productMap.get(cartProduct.getId());
            if(p == null){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName()+" 상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream().collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            //각 케이스 별로 에러를 쪼개고, 에러가 정상 출력 되야 하는지 체크해야 한다.
            List<String> tmpMessages = new ArrayList<>();

            for (int j = 0 ;  j < cartProduct.getItems().size();j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());
                if(pi == null){
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessages.add(cartProductItem.getName()+" 옵션이 삭제되었습니다.");
                    continue;
                }


                boolean isPriceChanged = false, isCountNotEnough = false;
                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChanged = true;
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                }
                if(isPriceChanged && isCountNotEnough){
                    // message 1
                    cartProductItem.setPrice(pi.getPrice());
                    cartProductItem.setCount(pi.getCount());
                    tmpMessages.add(cartProductItem.getName()+" 가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if(isPriceChanged){
                    // message 2
                    cartProductItem.setPrice(pi.getPrice());
                    tmpMessages.add(cartProductItem.getName()+" 가격이 변동되었습니다.");
                } else if(isCountNotEnough){
                    // message 3
                    cartProductItem.setCount(pi.getCount());
                    tmpMessages.add(cartProductItem.getName()+" 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }
            // 아이템이 모두 삭제 되었을 경우
            if(cartProduct.getItems().size()==0){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName()+" 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
            }
            else  if(tmpMessages.size()>0){
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName()+" 상품의 변동 사항 :");
                for(String message : tmpMessages){
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }

            return cart;
    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {

        Cart.Product cartProduct = cart.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));


        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;
                });


    }
}
