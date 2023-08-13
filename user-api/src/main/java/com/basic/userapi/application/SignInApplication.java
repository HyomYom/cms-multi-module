package com.basic.userapi.application;

import com.basic.domain.common.UserType;
import com.basic.domain.config.JwtAuthenticationProvider;
import com.basic.userapi.domain.SignInForm;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.domain.model.Seller;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.service.customer.CustomerService;
import com.basic.userapi.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.basic.domain.common.UserType.*;
import static com.basic.userapi.exception.ErrorCode.LOGIN_CHECK_FAIL;


@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;

    public String customerLoginToken(SignInForm form) {
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        return provider.createToken(c.getEmail(), c.getId(), CUSTOMER);
    }

    public String sellerLoginToken(SignInForm form) {
        Seller s = sellerService.findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        return provider.createToken(s.getEmail(), s.getId(), SELLER);
    }
}
