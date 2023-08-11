package com.basic.userapi.application;

import com.basic.domain.common.UserType;
import com.basic.domain.config.JwtAuthenticationProvider;
import com.basic.userapi.domain.SignInForm;
import com.basic.userapi.domain.model.Customer;
import com.basic.userapi.exception.CustomException;
import com.basic.userapi.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.basic.userapi.exception.ErrorCode.LOGIN_CHECK_FAIL;


@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm form){
        //1. 로그인 가능 여부
        Customer c = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));
        //2. 토큰을 발행하고
        //3. 토큰을 response한다.
         return provider.createToken(c.getEmail(), c.getId() , UserType.CUSTOMER);
    }
}
